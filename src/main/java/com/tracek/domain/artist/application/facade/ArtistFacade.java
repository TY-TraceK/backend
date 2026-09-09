package com.tracek.domain.artist.application.facade;

import com.tracek.domain.artist.application.dto.ArtistDetailResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.service.ContentArtistQueryService;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import java.util.List;
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
}
