package com.tracek.domain.content.application.facade;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.content.application.dto.ContentDetailResult;
import com.tracek.domain.content.application.service.ContentArtistQueryService;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContentFacade {
    private final ContentQueryService contentQueryService;
    private final LocationQueryService locationQueryService;
    private final ArtistQueryService artistQueryService;
    private final EpisodeQueryService episodeQueryService;
    private final ContentArtistQueryService contentArtistQueryService;

    // 메인 콘텐츠 상세 정보 조회 (플랫 형태로 연관 locations, artists)
    public ContentDetailResult getContentDetails(Long contentId) {
        Content content = contentQueryService.getContentEntity(contentId);

        // IN 절 배치 조회를 위한 Location ID / Artist ID 추출
        List<Long> artistIds = contentArtistQueryService.findArtistIdsByContentId(contentId);
        List<Long> locationIds = episodeQueryService.getLocationIdsByContentId(contentId);

        // IN 절 Batch Query로 N+1 문제 최적화 조회
        List<LocationResult> locations = locationQueryService.getLocationByIds(locationIds);
        List<ArtistResult> artists = artistQueryService.getArtistsByIds(artistIds);

        // ContentDetailResult.LocationResult 합성
        List<ContentDetailResult.LocationResult> locationResults =
                locations.stream().map(ContentDetailResult.LocationResult::of).toList();

        List<ContentDetailResult.ArtistResult> artistResults =
                artists.stream().map(ContentDetailResult.ArtistResult::of).toList();

        return ContentDetailResult.from(
                ContentDetailResult.ContentInfo.of(content), locationResults, artistResults);
    }
}
