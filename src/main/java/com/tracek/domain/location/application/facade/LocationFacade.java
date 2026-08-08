package com.tracek.domain.location.application.facade;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.image.application.service.ImageQueryService;
import com.tracek.domain.location.application.dto.LocationDetailResult;
import com.tracek.domain.location.application.dto.LocationRelatedInfoResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.domain.model.Location;
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
public class LocationFacade {
    private final LocationQueryService locationQueryService;
    private final ContentQueryService contentQueryService;
    private final ArtistQueryService artistQueryService;
    private final ImageQueryService imageQueryService;

    // 메인 관광지 상세 정보 조회 (계층형 데이터 그룹핑)
    public LocationDetailResult getLocationDetails(Long locationId) {

        // 관광지 엔티티 & 사진 URL 목록 조회
        Location location = locationQueryService.getLocationEntity(locationId);

        List<LocationDetailResult.LocationImageResult> imageResults =
                location.getImageLocations().stream()
                        .map(m-> {
                            ImageResult imageResult = imageQueryService.getImage(m.getImage().getId());
                            return LocationDetailResult.LocationImageResult.of(imageResult, m.getIsMain(), m.getDisplayOrder());
                        })
                        .toList();

        // 관광지 ID 기준 매핑 정보(LocationContentArtist) 전체 조회
        List<LocationContentArtist> mappings = locationQueryService.getMappingsByLocationId(locationId);

        // IN 절 배치 조회를 위한 Content ID / Artist ID 추출
        List<Long> contentIds = mappings.stream().map(m -> m.getContent().getId()).distinct().toList();
        List<Long> artistIds = mappings.stream().map(m -> m.getArtist().getId()).distinct().toList();

        // IN 절 Batch Query로 N+1 문제 최적화 조회
        List<ContentResult> contents = contentQueryService.getContentsByIds(contentIds);
        List<ArtistResult> artists = artistQueryService.getArtistsByIds(artistIds);

        // O(1) 탐색을 위해 ArtistResult ->  Application ArtistResult 맵 변환
        Map<Long, LocationDetailResult.ArtistResult> artistResultMap = artists.stream()
                .collect(Collectors.toMap(
                        ArtistResult::getId,
                        LocationDetailResult.ArtistResult::from
                ));

        // Content ID 기준으로 연관된 ArtistResult들을 리스트로 그룹핑
        Map<Long, List<LocationDetailResult.ArtistResult>> contentArtistGroupMap = mappings.stream()
                .collect(Collectors.groupingBy(
                        m->m.getContent().getId(),
                        Collectors.mapping(
                                m->artistResultMap.get(m.getArtist().getId()),
                                Collectors.filtering(Objects::nonNull, Collectors.toList())
                        )
                ));

        // LocationDetailResult.ContentResult 합성
        List<LocationDetailResult.ContentResult> contentResults = contents.stream()
                .map(content -> {
                    List<LocationDetailResult.ArtistResult> relatedArtists =
                            contentArtistGroupMap.getOrDefault(content.getContentId(), List.of());
                    return LocationDetailResult.ContentResult.of(content, relatedArtists);
                })
                .toList();

        return LocationDetailResult.from(LocationDetailResult.LocationInfo.of(location), imageResults, contentResults);

    }


    // 관광지 관련 데이터(콘텐츠-아티스트) 조회 -> 배치 조회 (N+1 개선)
    public LocationRelatedInfoResult getRelatedContentAndArtists(Long locationId) {
        Location location = locationQueryService.getLocationEntity(locationId);

        // 1. 매핑 데이터 가져오기.
        List<LocationContentArtist> mappings =
                locationQueryService.getMappingsByLocationId(locationId);

        // 2. ID 목록만 수집
        List<Long> contentIds =
                mappings.stream().map(m -> m.getContent().getId()).distinct().toList();
        List<Long> artistIds =
                mappings.stream().map(m -> m.getArtist().getId()).distinct().toList();

        // 3. 배치 조회 IN 절 쿼리 1번씩 묶어서 가져오기.
        List<ContentResult> contents = contentQueryService.getContentsByIds(contentIds);
        List<ArtistResult> artists = artistQueryService.getArtistsByIds(artistIds);

        // 4. Map 변환 (ID -> DTO)
        Map<Long, ContentResult> contentMap =
                contents.stream().collect(Collectors.toMap(ContentResult::getContentId, c -> c));
        Map<Long, ArtistResult> artistMap =
                artists.stream().collect(Collectors.toMap(ArtistResult::getId, a -> a));

        // 5. 조립
        List<LocationRelatedInfoResult.RelatedItemResult> relatedItems =
                mappings.stream()
                        .map(
                                m -> {
                                    ContentResult content = contentMap.get(m.getContent().getId());
                                    ArtistResult artist = artistMap.get(m.getArtist().getId());

                                    return LocationRelatedInfoResult.RelatedItemResult.of(
                                            content.getContentId(),
                                            content.getTitle(),
                                            content.getCategory(),
                                            content.getPictureUrl(),
                                            artist.getId(),
                                            artist.getName(),
                                            artist.getPictureUrl());
                                })
                        .collect(Collectors.toList());

        return LocationRelatedInfoResult.of(location.getId(), location.getName(), relatedItems);
    }
}
