package com.tracek.domain.artist.application.facade;

import com.tracek.domain.artist.application.dto.ArtistDetailResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.domain.model.LocationContentArtist;
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
public class ArtistFacade {
    private final ArtistQueryService artistQueryService;
    private final LocationQueryService locationQueryService;
    private final ContentQueryService contentQueryService;

    // 메인 아티스트 상세 정보 조회 (계층형 데이터 그룹핑: 콘텐츠 -> 관광지)
    public ArtistDetailResult getArtistDetails(Long artistId) {
        Artist artist = artistQueryService.getArtistEntity(artistId);

        // 아티스트 ID 기준 매핑 정보(LocationContentArtist) 전체 조회
        List<LocationContentArtist> mappings = locationQueryService.getMappingByArtistId(artistId);

        // IN 절 배치 조회를 위한 Content ID / Location ID 추출
        List<Long> contentIds =
                mappings.stream().map(m -> m.getContent().getId()).distinct().toList();
        List<Long> locationIds =
                mappings.stream().map(m -> m.getLocation().getId()).distinct().toList();

        // IN 절 Batch Query로 N+1 문제 최적화 조회
        List<ContentResult> contents = contentQueryService.getContentsByIds(contentIds);
        List<LocationResult> locations = locationQueryService.getLocationByIds(locationIds);

        // O(1) 탐색을 위해 LocationResult 맵 변환
        Map<Long, LocationResult> locationResultMap =
                locations.stream().collect(Collectors.toMap(LocationResult::getLocationId, l -> l));

        // Content ID 기준으로 연관된 LocationResult들을 리스트로 그룹핑 (매핑 PK도 함께 전달)
        Map<Long, List<ArtistDetailResult.LocationResult>> contentLocationGroupMap =
                mappings.stream()
                        .collect(
                                Collectors.groupingBy(
                                        m -> m.getContent().getId(),
                                        Collectors.mapping(
                                                m -> {
                                                    LocationResult location =
                                                            locationResultMap.get(
                                                                    m.getLocation().getId());
                                                    return location == null
                                                            ? null
                                                            : ArtistDetailResult.LocationResult
                                                                    .from(m.getId(), location);
                                                },
                                                Collectors.filtering(
                                                        Objects::nonNull, Collectors.toList()))));

        // ArtistDetailResult.ContentResult 합성
        List<ArtistDetailResult.ContentResult> contentResults =
                contents.stream()
                        .map(
                                content -> {
                                    List<ArtistDetailResult.LocationResult> relatedLocations =
                                            contentLocationGroupMap.getOrDefault(
                                                    content.getContentId(), List.of());
                                    return ArtistDetailResult.ContentResult.of(
                                            content, relatedLocations);
                                })
                        .toList();

        return ArtistDetailResult.from(ArtistDetailResult.ArtistInfo.of(artist), contentResults);
    }
}
