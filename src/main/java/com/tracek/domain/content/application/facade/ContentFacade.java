package com.tracek.domain.content.application.facade;

import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.content.application.dto.ContentDetailResult;
import com.tracek.domain.content.application.dto.EpisodeResult;
import com.tracek.domain.content.application.service.ContentArtistQueryService;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.Episode;
import com.tracek.domain.content.domain.model.EpisodeLocation;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RelatedLocationRankingResult;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContentFacade {
    private final ContentQueryService contentQueryService;
    private final ContentArtistQueryService contentArtistQueryService;
    private final LocationQueryService locationQueryService;
    private final ArtistQueryService artistQueryService;
    private final EpisodeQueryService episodeQueryService;
    private final VisitRankingQueryService visitRankingQueryService;

    // 메인 콘텐츠 상세 정보 조회 (콘텐츠 정보 + 고정 출연진, 연관 관광지는 방문 인증 랭킹 순)
    public ContentDetailResult getContentDetails(
            Long contentId, String city, RankingCondition condition) {
        Content content = contentQueryService.getContentEntity(contentId);

        // 고정 출연 아티스트
        List<Long> fixedArtistIds =
                contentArtistQueryService.findFixedArtistIdsByContentId(contentId);
        List<ContentDetailResult.FixedArtistResult> fixedArtists =
                artistQueryService.getArtistsByIds(fixedArtistIds).stream()
                        .map(ContentDetailResult.FixedArtistResult::from)
                        .toList();

        // content -> 연관 locations 조회 (랭킹 순)
        List<RelatedLocationRankingResult> relatedLocationRankings =
                visitRankingQueryService.getLocationsByContent(contentId, condition).rankings();
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
                episodeQueryService.getEpisodesByContentId(contentId).stream()
                        .collect(
                                Collectors.groupingBy(
                                        episodeLocation -> episodeLocation.getLocation().getId(),
                                        Collectors.mapping(
                                                EpisodeLocation::getEpisode, Collectors.toList())));

        // 재정렬 및 합성
        List<ContentDetailResult.LocationResult> locationResults =
                relatedLocationRankings.stream()
                        .map(
                                ranking -> {
                                    LocationResult location = locationMap.get(ranking.locationId());
                                    if (location == null) {
                                        return null;
                                    }
                                    return ContentDetailResult.LocationResult.of(
                                            location,
                                            ranking.totalVerificationCount(),
                                            episodesByLocationId
                                                    .getOrDefault(ranking.locationId(), List.of())
                                                    .stream()
                                                    .map(
                                                            episode ->
                                                                    ContentDetailResult
                                                                            .EpisodeResult.from(
                                                                            EpisodeResult.from(
                                                                                    episode)))
                                                    .toList());
                                })
                        .filter(Objects::nonNull)
                        .toList();

        return ContentDetailResult.of(
                ContentDetailResult.ContentInfo.of(content, fixedArtists), locationResults);
    }

    // 콘텐츠 -> 연관 장소 최신 등록순 조회 (최대 size개)
    public List<LocationResult> getLatestLocationsByContent(Long contentId, int size) {
        List<Long> latestLocationIds =
                episodeQueryService.getLatestLocationIdsByContentId(contentId, size);

        // JPA get- 쿼리는 입력 id 순서 보장 X, 최신순 재정렬 필요
        Map<Long, LocationResult> locationMap =
                locationQueryService.getLocationByIds(latestLocationIds).stream()
                        .collect(Collectors.toMap(LocationResult::getLocationId, l -> l));

        return latestLocationIds.stream().map(locationMap::get).filter(Objects::nonNull).toList();
    }
}
