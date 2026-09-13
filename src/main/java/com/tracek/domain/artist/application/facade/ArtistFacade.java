package com.tracek.domain.artist.application.facade;

import com.tracek.domain.artist.application.dto.ArtistDetailRelatedContentResult;
import com.tracek.domain.artist.application.dto.ArtistDetailRelatedLocationResult;
import com.tracek.domain.artist.application.dto.ArtistDetailResult;
import com.tracek.domain.artist.application.dto.ArtistSummaryResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.dto.EpisodeResult;
import com.tracek.domain.content.application.service.ContentArtistQueryService;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.content.domain.model.Episode;
import com.tracek.domain.content.domain.model.EpisodeLocation;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RelatedContentRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedLocationRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedMultiRankingResult;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtistFacade {
    private final ArtistQueryService artistQueryService;
    private final LocationQueryService locationQueryService;
    private final ContentQueryService contentQueryService;
    private final ContentArtistQueryService contentArtistQueryService;
    private final EpisodeQueryService episodeQueryService;
    private final VisitRankingQueryService visitRankingQueryService;

    // 메인 아티스트 상세 정보 조회 (플랫 구조 : 관광지 목록, 콘텐츠 목록)
    public ArtistDetailResult getArtistDetails(Long artistId) {
        Artist artist = artistQueryService.getArtistEntity(artistId);

        // 아티스트 ID 기준 매핑 정보 전체 조회

        // IN 절 배치 조회를 위한 Content ID / Location ID 추출
        List<Long> contentIds = contentArtistQueryService.findContentIdsByArtistId(artistId);

        List<Long> locationIds = episodeQueryService.getLocationIdsByArtistId(artistId);

        // IN 절 Batch Query로 N+1 문제 최적화 조회
        List<ContentResult> contents = contentQueryService.getContentsByIds(contentIds);
        List<LocationResult> locations = locationQueryService.getLocationByIds(locationIds);

        // ArtistDetailResult.LocationResult 합성
        List<ArtistDetailResult.LocationResult> locationResults =
                locations.stream().map(ArtistDetailResult.LocationResult::of).toList();

        // ArtistDetailResult.ContentResult 합성
        List<ArtistDetailResult.ContentResult> contentResults =
                contents.stream().map(ArtistDetailResult.ContentResult::of).toList();

        return ArtistDetailResult.from(
                ArtistDetailResult.ArtistInfo.of(artist), locationResults, contentResults);
    }

    // 아티스트 상세 정보 조회 - 연관 관광지 탭
    public ArtistDetailRelatedLocationResult getArtistDetailsRelatedLocation(
            Long artistId, String city, RankingCondition condition) {
        Artist artist = artistQueryService.getArtistEntity(artistId);
        // group -> members / member -> group
        List<ArtistSummaryResult> relatedArtists =
                Boolean.TRUE.equals(artist.getIsGroup())
                        ? artistQueryService.findByGroupId(artistId).stream()
                                .map(ArtistSummaryResult::from)
                                .toList()
                        : artist.getGroup() == null
                                ? List.of()
                                : List.of(ArtistSummaryResult.from(artist.getGroup()));

        // artist -> 연관 locations 조회
        // List(locationId, totalVerificationCount)
        List<RelatedLocationRankingResult> relatedLocationRankings =
                visitRankingQueryService.getLocationsByArtist(artistId, condition).rankings();
        List<Long> sortedLocationIds =
                relatedLocationRankings.stream()
                        .map(RelatedLocationRankingResult::locationId)
                        .distinct()
                        .toList();

        // IN 절 Batch Query로 N+1 문제 최적화 조회
        // JPA get- 쿼리는 입력 id 순서 보장 X
        List<LocationResult> locations = locationQueryService.getLocationByIds(sortedLocationIds);

        // 재정렬을 위한 맵 with city 필터링
        Map<Long, LocationResult> locationMap =
                locations.stream()
                        .filter(l -> city == null || city.equals(l.getAddress().getCity()))
                        .collect(Collectors.toMap(LocationResult::getLocationId, l -> l));

        // Batch 조회로 (locationId, List<relatedEpisode>) Map 미리 생성
        Map<Long, List<Episode>> episodesByLocationId =
                episodeQueryService
                        .getEpisodesByArtistAndLocationIds(artistId, sortedLocationIds)
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        episodeLocation -> episodeLocation.getLocation().getId(),
                                        Collectors.mapping(
                                                EpisodeLocation::getEpisode, Collectors.toList())));

        // 재정렬 및 합성
        List<ArtistDetailRelatedLocationResult.LocationResult> locationResults =
                relatedLocationRankings.stream()
                        .map(
                                ranking -> {
                                    LocationResult location = locationMap.get(ranking.locationId());
                                    if (location == null) {
                                        return null;
                                    }
                                    return ArtistDetailRelatedLocationResult.LocationResult.of(
                                            location,
                                            ranking.totalVerificationCount(),
                                            episodesByLocationId
                                                    .getOrDefault(ranking.locationId(), List.of())
                                                    .stream()
                                                    .map(
                                                            episode ->
                                                                    ArtistDetailRelatedLocationResult
                                                                            .EpisodeResult.from(
                                                                            EpisodeResult.from(
                                                                                    episode)))
                                                    .toList());
                                })
                        .filter(Objects::nonNull)
                        .toList();

        return ArtistDetailRelatedLocationResult.of(
                ArtistDetailRelatedLocationResult.ArtistInfo.of(artist, relatedArtists),
                locationResults);
    }

    public ArtistDetailRelatedContentResult getArtistDetailsRelatedContent(
            Long artistId, RankingCondition condition) {
        Artist artist = artistQueryService.getArtistEntity(artistId);
        // group -> members / member -> group
        List<ArtistSummaryResult> relatedArtists =
                Boolean.TRUE.equals(artist.getIsGroup())
                        ? artistQueryService.findByGroupId(artistId).stream()
                                .map(ArtistSummaryResult::from)
                                .toList()
                        : artist.getGroup() == null
                                ? List.of()
                                : List.of(ArtistSummaryResult.from(artist.getGroup()));

        // artist -> 연관 contents 조회 (랭킹 순)
        List<RelatedContentRankingResult> relatedContentRankings =
                visitRankingQueryService.getContentsByArtist(artistId, condition).rankings();
        List<Long> sortedContentIds =
                relatedContentRankings.stream()
                        .map(RelatedContentRankingResult::contentId)
                        .distinct()
                        .toList();

        // IN 절 Batch Query로 N+1 문제 최적화 조회
        Map<Long, ContentResult> contentMap =
                contentQueryService.getContentsByIds(sortedContentIds).stream()
                        .collect(Collectors.toMap(ContentResult::getContentId, c -> c));

        // isFixed 배치 조회 (contentId -> isFixed)
        Map<Long, Boolean> isFixedByContentId =
                contentArtistQueryService.findIsFixedByArtistIdAndContentIds(
                        artistId, sortedContentIds);

        // episode(+location) 배치 조회 -> contentId -> locationId -> episode 리스트로 이중 그룹핑
        Map<Long, Map<Long, List<Episode>>> episodesByContentThenLocation =
                episodeQueryService
                        .getEpisodesByArtistAndContentIds(artistId, sortedContentIds)
                        .stream()
                        .collect(
                                Collectors.groupingBy(
                                        el -> el.getEpisode().getContent().getId(),
                                        Collectors.groupingBy(
                                                el -> el.getLocation().getId(),
                                                Collectors.mapping(
                                                        EpisodeLocation::getEpisode,
                                                        Collectors.toList()))));

        // 위에서 나온 location들 배치 조회 (이름 등 표시용)
        List<Long> relatedLocationIds =
                episodesByContentThenLocation.values().stream()
                        .flatMap(m -> m.keySet().stream())
                        .distinct()
                        .toList();
        Map<Long, LocationResult> locationMap =
                locationQueryService.getLocationByIds(relatedLocationIds).stream()
                        .collect(Collectors.toMap(LocationResult::getLocationId, l -> l));

        // 방문 인증 카운트(장소 X 아티스트 X 콘텐츠) 배치 조회 -> contentId -> locationId -> count로 그룹핑
        Map<Long, Map<Long, Long>> visitCountByContentThenLocation =
                visitRankingQueryService.getMultiRankingByArtist(artistId).rankings().stream()
                        .collect(
                                Collectors.groupingBy(
                                        RelatedMultiRankingResult::contentId,
                                        Collectors.toMap(
                                                RelatedMultiRankingResult::locationId,
                                                RelatedMultiRankingResult
                                                        ::totalVerificationCount)));

        // 재정렬 및 합성
        List<ArtistDetailRelatedContentResult.ContentResult> contentResults =
                relatedContentRankings.stream()
                        .map(
                                ranking -> {
                                    ContentResult content = contentMap.get(ranking.contentId());
                                    if (content == null) {
                                        return null;
                                    }

                                    Map<Long, List<Episode>> episodesByLocation =
                                            episodesByContentThenLocation.getOrDefault(
                                                    ranking.contentId(), Map.of());

                                    Map<Long, Long> visitCountByLocation =
                                            visitCountByContentThenLocation.getOrDefault(
                                                    ranking.contentId(), Map.of());

                                    List<ArtistDetailRelatedContentResult.RelatedLocationResult>
                                            relatedLocations =
                                                    episodesByLocation.entrySet().stream()
                                                            .map(
                                                                    entry -> {
                                                                        Long locationId =
                                                                                entry.getKey();
                                                                        LocationResult location =
                                                                                locationMap.get(
                                                                                        locationId);
                                                                        if (location == null) {
                                                                            return null;
                                                                        }
                                                                        List<
                                                                                        ArtistDetailRelatedContentResult
                                                                                                .EpisodeResult>
                                                                                episodeResults =
                                                                                        entry
                                                                                                .getValue()
                                                                                                .stream()
                                                                                                .map(
                                                                                                        episode ->
                                                                                                                ArtistDetailRelatedContentResult
                                                                                                                        .EpisodeResult
                                                                                                                        .from(
                                                                                                                                EpisodeResult
                                                                                                                                        .from(
                                                                                                                                                episode)))
                                                                                                .toList();
                                                                        return ArtistDetailRelatedContentResult
                                                                                .RelatedLocationResult
                                                                                .of(
                                                                                        locationId,
                                                                                        location
                                                                                                .getName(),
                                                                                        visitCountByLocation
                                                                                                .getOrDefault(
                                                                                                        locationId,
                                                                                                        0L),
                                                                                        episodeResults);
                                                                    })
                                                            .filter(Objects::nonNull)
                                                            .toList();

                                    return ArtistDetailRelatedContentResult.ContentResult.of(
                                            content,
                                            isFixedByContentId.get(ranking.contentId()),
                                            relatedLocations);
                                })
                        .filter(Objects::nonNull)
                        .toList();

        return ArtistDetailRelatedContentResult.of(
                ArtistDetailRelatedContentResult.ArtistInfo.of(artist, relatedArtists),
                contentResults);
    }
}
