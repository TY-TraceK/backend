package com.tracek.domain.content.application.facade;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.content.application.dto.ContentDetailResult;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.domain.model.LocationContentArtist;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContentFacade {
    private final ContentQueryService contentQueryService;
    private final LocationQueryService locationQueryService;
    private final ArtistQueryService artistQueryService;

    // 메인 콘텐츠 상세 정보 조회 (계층형 데이터 그룹핑: 관광지 -> 아티스트)
    public ContentDetailResult getContentDetails(Long contentId) {
        Content content = contentQueryService.getContentEntity(contentId);

        // 콘텐츠 ID 기준 매핑 정보(LocationContentArtist) 전체 조회
        List<LocationContentArtist> mappings = locationQueryService.getMappingsByContentId(contentId);

        // IN 절 배치 조회를 위한 Location ID / Artist ID 추출
        List<Long> locationIds = mappings.stream().map(m -> m.getLocation().getId()).distinct().toList();
        List<Long> artistIds = mappings.stream().map(m -> m.getArtist().getId()).distinct().toList();

        // IN 절 Batch Query로 N+1 문제 최적화 조회
        List<LocationResult> locations = locationQueryService.getLocationByIds(locationIds);
        List<ArtistResult> artists = artistQueryService.getArtistsByIds(artistIds);

        // O(1) 탐색을 위해 ArtistResult -> ContentDetailResult.ArtistResult 맵 변환
        Map<Long, ContentDetailResult.ArtistResult> artistResultMap = artists.stream()
                .collect(Collectors.toMap(
                        ArtistResult::getId,
                        ContentDetailResult.ArtistResult::from
                ));

        // Location ID 기준으로 연관된 ArtistResult들을 리스트로 그룹핑
        Map<Long, List<ContentDetailResult.ArtistResult>> locationArtistGroupMap = mappings.stream()
                .collect(Collectors.groupingBy(
                        m -> m.getLocation().getId(),
                        Collectors.mapping(
                                m -> artistResultMap.get(m.getArtist().getId()),
                                Collectors.filtering(Objects::nonNull, Collectors.toList())
                        )
                ));

        // ContentDetailResult.LocationResult 합성
        List<ContentDetailResult.LocationResult> locationResults = locations.stream()
                .map(location -> {
                    List<ContentDetailResult.ArtistResult> relatedArtists =
                            locationArtistGroupMap.getOrDefault(location.getLocationId(), List.of());
                    return ContentDetailResult.LocationResult.of(location, relatedArtists);
                })
                .toList();

        return ContentDetailResult.from(ContentDetailResult.ContentInfo.of(content), locationResults);
    }
}
