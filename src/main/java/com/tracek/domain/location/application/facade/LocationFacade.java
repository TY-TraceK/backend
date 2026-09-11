package com.tracek.domain.location.application.facade;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.content.application.dto.ContentArtistPair;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.image.application.service.ImageQueryService;
import com.tracek.domain.location.application.dto.LocationDetailResult;
import com.tracek.domain.location.application.dto.LocationRelatedInfoResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.domain.model.Location;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.application.dto.result.RelatedArtistRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedContentRankingResult;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationFacade {
    private final LocationQueryService locationQueryService;
    private final ContentQueryService contentQueryService;
    private final ArtistQueryService artistQueryService;
    private final ImageQueryService imageQueryService;
    private final EpisodeQueryService episodeQueryService;
    private final VisitRankingQueryService visitRankingQueryService;

    // 메인 관광지 상세 정보 조회 (플랫 구조 - 연관 콘텐츠, 아티스트)
    public LocationDetailResult getLocationDetails(Long locationId, RankingCondition condition) {
        // 관광지 엔티티 & 사진 URL 목록 조회
        Location location = locationQueryService.getLocationEntity(locationId);

        // IN 절 Batch Query로 N+1 문제 최적화 조회
        List<Long> imageIds =
                location.getImageLocations().stream()
                        .map(m -> m.getImage().getId())
                        .distinct()
                        .toList();
        Map<Long, ImageResult> imageResultMap =
                imageQueryService.getImagesByIds(imageIds).stream()
                        .collect(Collectors.toMap(ImageResult::getId, imageResult -> imageResult));

        List<LocationDetailResult.LocationImageResult> imageResults =
                location.getImageLocations().stream()
                        .map(
                                m ->
                                        LocationDetailResult.LocationImageResult.of(
                                                imageResultMap.get(m.getImage().getId()),
                                                m.getIsMain(),
                                                m.getDisplayOrder()))
                        .toList();


        // 장소 -> 연관된 콘텐츠 조회
        RankingSliceResult<RelatedContentRankingResult> relatedContentRankingResult = visitRankingQueryService.getContentsByLocation(locationId, condition);
        List<RelatedContentRankingResult> relatedContentRankings = relatedContentRankingResult.rankings();
        // 장소 -> 연관된 아티스트 조회
        RankingSliceResult<RelatedArtistRankingResult> relatedArtistRankingResult = visitRankingQueryService.getArtistsByLocation(locationId, condition);
        List<RelatedArtistRankingResult> relatedArtistRankings = relatedArtistRankingResult.rankings();

        // relatedContentRankingResult.rankings() -> List<RelatedContentRankingResult> -> List(contentId, totalVerificationCount)
        List<Long> sortedContentIds = relatedContentRankings.stream()
                .map(RelatedContentRankingResult::contentId).distinct().toList();

        // relatedArtistRankingResult.rankings() -> List<RelatedArtistRankingResult> -> List(artistId, totalVerificationCount)
        List<Long> sortedArtistIds = relatedArtistRankings.stream()
                .map(RelatedArtistRankingResult::artistId).distinct().toList();

        // IN 절 Batch Query로 N+1 문제 최적화 조회
        // JPA get- 쿼리는 입력 id 순서 보장 X
        List<ContentResult> contents = contentQueryService.getContentsByIds(sortedContentIds);
        List<ArtistResult> artists = artistQueryService.getArtistsByIds(sortedArtistIds);

        // id 재정렬을 위한 Map
        Map<Long, ContentResult> contentResultMap = contents.stream()
                .collect(Collectors.toMap(
                        ContentResult::getContentId,
                        content -> content
                ));
        Map<Long, ArtistResult> artistResultMap = artists.stream()
                .collect(Collectors.toMap(
                        ArtistResult::getId,
                        artist -> artist
                ));

        // 재정렬 및 합성
        List<LocationDetailResult.ContentResult> contentResults = relatedContentRankings.stream()
                .map(ranking -> {
                   ContentResult content = contentResultMap.get(ranking.contentId());
                   return content == null ? null : LocationDetailResult.ContentResult.of(content, ranking.totalVerificationCount());
                }).filter(Objects::nonNull)
                .toList();
        List<LocationDetailResult.ArtistResult> artistResults = relatedArtistRankings.stream()
                .map(   ranking -> {
                    ArtistResult artist = artistResultMap.get(ranking.artistId());
                    return artist == null ? null : LocationDetailResult.ArtistResult.of(artist, ranking.totalVerificationCount());
                }).filter(Objects::nonNull)
                .toList();

        return LocationDetailResult.of(
                LocationDetailResult.LocationInfo.from(location), imageResults, contentResults, artistResults);
    }

    // 관광지 관련 데이터(콘텐츠-아티스트) 조회 -> 배치 조회 (N+1 개선)
    public LocationRelatedInfoResult getRelatedContentAndArtists(Long locationId) {
        Location location = locationQueryService.getLocationEntity(locationId);

        // 1. 콘텐츠-아티스트 쌍 id 가져오기.
        List<ContentArtistPair> pairs = episodeQueryService.getContentArtistPairs(locationId);
        List<Long> contentIds =
                pairs.stream().map(ContentArtistPair::getContentId).distinct().toList();
        List<Long> artistIds =
                pairs.stream().map(ContentArtistPair::getArtistId).distinct().toList();

        // 2. 콘텐츠 id-> 아티스트 id 그룹핑
        Map<Long, List<Long>> artistIdsByContentId =
                pairs.stream()
                        .collect(
                                Collectors.groupingBy(
                                        ContentArtistPair::getContentId,
                                        Collectors.mapping(
                                                ContentArtistPair::getArtistId,
                                                Collectors.toList())));
        // 3. 배치 조회 (In절, N+1 방지)
        List<ContentResult> contents = contentQueryService.getContentsByIds(contentIds);
        Map<Long, ArtistResult> artistResultMap =
                artistQueryService
                        .getArtistsByIds(artistIds)
                        .stream() // IN절로 List<ArtistResult> 가져오기
                        .collect(
                                Collectors.toMap(
                                        ArtistResult::getId,
                                        a -> a)); // Map 변환(ArtistResult의 id : ArtistResult)

        // 3. 리스트 변환
        List<LocationRelatedInfoResult.RelatedContentGroup> groups =
                contents.stream()
                        .map(
                                content -> {
                                    List<LocationRelatedInfoResult.RelatedArtistResult> artists =
                                            artistIdsByContentId
                                                    .getOrDefault(
                                                            content.getContentId(),
                                                            List.of()) // contentId : (artistId
                                                    // : artist)
                                                    .stream()
                                                    .map(artistResultMap::get)
                                                    .filter(Objects::nonNull)
                                                    .map(
                                                            a ->
                                                                    LocationRelatedInfoResult
                                                                            .RelatedArtistResult.of(
                                                                            a.getId(),
                                                                            a.getName(),
                                                                            a.getPictureUrl(),
                                                                            a.getIsGroup()))
                                                    .toList();

                                    return LocationRelatedInfoResult.RelatedContentGroup.of(
                                            content.getContentId(),
                                            content.getTitle(),
                                            content.getCategory(),
                                            content.getPictureUrl(),
                                            artists);
                                })
                        .toList();

        return LocationRelatedInfoResult.of(
                location.getId(), location.getName(), location.getAddress().getCity(), groups);
    }
}
