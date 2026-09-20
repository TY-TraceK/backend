package com.tracek.domain.location.application.facade;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.content.application.dto.ContentArtistPair;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.service.ContentArtistQueryService;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.image.application.service.ImageQueryService;
import com.tracek.domain.location.application.client.TourImageClient;
import com.tracek.domain.location.application.client.TourLocationDetailClient;
import com.tracek.domain.location.application.dto.LocationDetailResult;
import com.tracek.domain.location.application.dto.LocationRelatedInfoResult;
import com.tracek.domain.location.application.dto.LocationSummaryResult;
import com.tracek.domain.location.application.dto.LocationTopSavedResult;
import com.tracek.domain.location.application.dto.TourImageResult;
import com.tracek.domain.location.application.dto.TourLocationDetailResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.application.dto.result.RelatedArtistRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedContentRankingResult;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationFacade {
    private static final String TOUR_API_SOURCE_TYPE = "TOUR_API";

    private final LocationQueryService locationQueryService;
    private final ContentQueryService contentQueryService;
    private final ArtistQueryService artistQueryService;
    private final ImageQueryService imageQueryService;
    private final EpisodeQueryService episodeQueryService;
    private final VisitRankingQueryService visitRankingQueryService;
    private final ContentArtistQueryService contentArtistQueryService;
    private final TourImageClient tourImageClient;
    private final TourLocationDetailClient tourLocationDetailClient;

    // 메인 관광지 상세 정보 조회 (플랫 구조 - 연관 콘텐츠, 아티스트)
    public LocationDetailResult getLocationDetails(
            Long locationId, RankingCondition condition, Long userId) {
        // 관광지 엔티티 & 사진 URL 목록 조회
        Location location = locationQueryService.getLocationEntity(locationId);

        List<LocationDetailResult.LocationImageResult> imageResults = getImageResults(location);

        // 장소 -> 연관된 콘텐츠 조회
        RankingSliceResult<RelatedContentRankingResult> relatedContentRankingResult =
                visitRankingQueryService.getContentsByLocation(locationId, condition);
        List<RelatedContentRankingResult> relatedContentRankings =
                relatedContentRankingResult.rankings();
        // 장소 -> 연관된 아티스트 조회
        RankingSliceResult<RelatedArtistRankingResult> relatedArtistRankingResult =
                visitRankingQueryService.getArtistsByLocation(locationId, condition);
        List<RelatedArtistRankingResult> relatedArtistRankings =
                relatedArtistRankingResult.rankings();

        // relatedContentRankingResult.rankings() -> List<RelatedContentRankingResult> ->
        // List(contentId, totalVerificationCount)
        List<Long> sortedContentIds =
                relatedContentRankings.stream()
                        .map(RelatedContentRankingResult::contentId)
                        .distinct()
                        .toList();

        // relatedArtistRankingResult.rankings() -> List<RelatedArtistRankingResult> ->
        // List(artistId, totalVerificationCount)
        List<Long> sortedArtistIds =
                relatedArtistRankings.stream()
                        .map(RelatedArtistRankingResult::artistId)
                        .distinct()
                        .toList();

        // IN 절 Batch Query로 N+1 문제 최적화 조회
        // JPA get- 쿼리는 입력 id 순서 보장 X
        List<ContentResult> contents = contentQueryService.getContentsByIds(sortedContentIds);
        List<ArtistResult> artists = artistQueryService.getArtistsByIds(sortedArtistIds);

        // id 재정렬을 위한 Map
        Map<Long, ContentResult> contentResultMap =
                contents.stream()
                        .collect(Collectors.toMap(ContentResult::getContentId, content -> content));
        Map<Long, ArtistResult> artistResultMap =
                artists.stream().collect(Collectors.toMap(ArtistResult::getId, artist -> artist));

        // 재정렬 및 합성
        List<LocationDetailResult.ContentResult> contentResults =
                relatedContentRankings.stream()
                        .map(
                                ranking -> {
                                    ContentResult content =
                                            contentResultMap.get(ranking.contentId());
                                    return content == null
                                            ? null
                                            : LocationDetailResult.ContentResult.of(
                                                    content, ranking.totalVerificationCount());
                                })
                        .filter(Objects::nonNull)
                        .toList();
        List<LocationDetailResult.ArtistResult> artistResults =
                relatedArtistRankings.stream()
                        .map(
                                ranking -> {
                                    ArtistResult artist = artistResultMap.get(ranking.artistId());
                                    return artist == null
                                            ? null
                                            : LocationDetailResult.ArtistResult.of(
                                                    artist, ranking.totalVerificationCount());
                                })
                        .filter(Objects::nonNull)
                        .toList();

        boolean isLiked = locationQueryService.isLikedByUser(userId, locationId);
        boolean isArchived = locationQueryService.isArchivedByUser(userId, locationId);
        TourLocationDetailResult tourDetail = getTourLocationDetail(location);
        String overview =
                hasText(tourDetail == null ? null : tourDetail.getOverview())
                        ? tourDetail.getOverview()
                        : location.getOverview();
        String tel =
                hasText(tourDetail == null ? null : tourDetail.getTel())
                        ? tourDetail.getTel()
                        : location.getTel();

        return LocationDetailResult.of(
                LocationDetailResult.LocationInfo.from(
                        location, isLiked, isArchived, overview, tel),
                imageResults,
                contentResults,
                artistResults);
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

        // contentId -> (artistId -> isFixed) 배치 조회
        Map<Long, Map<Long, Boolean>> isFixedByContentThenArtist =
                contentArtistQueryService.findIsFixedByContentIds(contentIds);

        // 3. 리스트 변환
        List<LocationRelatedInfoResult.RelatedContentGroup> groups =
                contents.stream()
                        .map(
                                content -> {
                                    Map<Long, Boolean> isFixedByArtistId =
                                            isFixedByContentThenArtist.getOrDefault(
                                                    content.getContentId(), Map.of());

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
                                                                            a.getIsGroup(),
                                                                            isFixedByArtistId.get(
                                                                                    a.getId())))
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

    // 좋아요+북마크 합산 TOP N 관광지 조회 (연관 콘텐츠 방문 인증 top3, 로그인 유저의 북마크 여부 포함)
    public List<LocationTopSavedResult> getTopSavedLocations(Long userId, int limit) {
        List<LocationSummaryResult> topLocations = locationQueryService.getTopSavedLocations(limit);

        RankingCondition top3Condition = new RankingCondition(null, null, 3);

        // 관광지별 연관 콘텐츠 방문 인증 top3 랭킹 조회
        Map<Long, List<RelatedContentRankingResult>> rankingsByLocationId =
                topLocations.stream()
                        .collect(
                                Collectors.toMap(
                                        LocationSummaryResult::getId,
                                        location ->
                                                visitRankingQueryService
                                                        .getContentsByLocation(
                                                                location.getId(), top3Condition)
                                                        .rankings()));

        List<Long> allContentIds =
                rankingsByLocationId.values().stream()
                        .flatMap(List::stream)
                        .map(RelatedContentRankingResult::contentId)
                        .distinct()
                        .toList();

        // IN 절 Batch Query로 N+1 문제 최적화 조회
        Map<Long, String> contentTitleById =
                contentQueryService.getContentsByIds(allContentIds).stream()
                        .collect(
                                Collectors.toMap(
                                        ContentResult::getContentId, ContentResult::getTitle));

        return topLocations.stream()
                .map(
                        location -> {
                            List<String> relatedContentTitles =
                                    rankingsByLocationId
                                            .getOrDefault(location.getId(), List.of())
                                            .stream()
                                            .map(
                                                    ranking ->
                                                            contentTitleById.get(
                                                                    ranking.contentId()))
                                            .filter(Objects::nonNull)
                                            .toList();
                            boolean isArchived =
                                    locationQueryService.isArchivedByUser(userId, location.getId());
                            return LocationTopSavedResult.of(
                                    location, relatedContentTitles, isArchived);
                        })
                .toList();
    }

    // 사진 정보: TourAPI 실시간 호출 우선, 실패(또는 대상 아님) 시 DB(ImageLocation)로 폴백
    private List<LocationDetailResult.LocationImageResult> getImageResults(Location location) {
        if (TOUR_API_SOURCE_TYPE.equals(location.getSourceType())
                && location.getExternalContentId() != null) {
            try {
                List<TourImageResult> tourImages =
                        tourImageClient.getImages(location.getExternalContentId());
                if (!tourImages.isEmpty()) {
                    return IntStream.range(0, tourImages.size())
                            .mapToObj(
                                    i ->
                                            LocationDetailResult.LocationImageResult.ofTourApi(
                                                    tourImages.get(i), i == 0, i + 1))
                            .toList();
                }
            } catch (Exception e) {
                log.warn(
                        "TourAPI 이미지 조회 실패, DB로 폴백합니다. locationId={}, externalContentId={}",
                        location.getId(),
                        location.getExternalContentId(),
                        e);
            }
        }
        return getImageResultsFromDb(location);
    }

    private List<LocationDetailResult.LocationImageResult> getImageResultsFromDb(
            Location location) {
        // IN 절 Batch Query로 N+1 문제 최적화 조회
        List<Long> imageIds =
                location.getImageLocations().stream()
                        .map(m -> m.getImage().getId())
                        .distinct()
                        .toList();
        Map<Long, ImageResult> imageResultMap =
                imageQueryService.getImagesByIds(imageIds).stream()
                        .collect(Collectors.toMap(ImageResult::getId, imageResult -> imageResult));

        return location.getImageLocations().stream()
                .map(
                        m ->
                                LocationDetailResult.LocationImageResult.of(
                                        imageResultMap.get(m.getImage().getId()),
                                        m.getIsMain(),
                                        m.getDisplayOrder()))
                .toList();
    }

    // 개요/전화번호: TourAPI(detailCommon2) 실시간 호출 우선, 실패(또는 대상 아님) 시 null 반환 -> 호출부에서 DB 값으로 폴백
    private TourLocationDetailResult getTourLocationDetail(Location location) {
        if (!TOUR_API_SOURCE_TYPE.equals(location.getSourceType())
                || location.getExternalContentId() == null) {
            return null;
        }
        try {
            return tourLocationDetailClient.getDetail(location.getExternalContentId());
        } catch (Exception e) {
            log.warn(
                    "TourAPI 상세 정보 조회 실패, DB로 폴백합니다. locationId={}, externalContentId={}",
                    location.getId(),
                    location.getExternalContentId(),
                    e);
            return null;
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
