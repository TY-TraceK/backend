package com.tracek.domain.location.application.service;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.image.application.service.ImageQueryService;
import com.tracek.domain.location.application.dto.LocationNearbyResult;
import com.tracek.domain.location.application.dto.LocationRelatedInfoResult;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.domain.location.domain.model.GeoLocation;
import com.tracek.domain.location.domain.model.ImageLocation;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationContentArtist;
import com.tracek.domain.location.domain.repository.LocationRepository;
import com.tracek.global.exception.CustomException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LocationQueryService {
    private final LocationRepository locationRepository;
    private final ContentQueryService contentQueryService;
    private final ArtistQueryService artistQueryService;
    private final ImageQueryService imageQueryService;

    // 단건 관광지 상세 조회
    public LocationResult getLocation(Long locationId) {
        Location location =
                locationRepository
                        .findById(locationId)
                        .orElseThrow(
                                () -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));
        // 관광지 이미지 변환
        List<LocationResult.LocationImageResult> imageResults =
                location.getImageLocations().stream()
                        .map(this::toLocationImageResult)
                        .collect(Collectors.toList());
        // 연관 매핑 조회
        List<LocationContentArtist> mappings =
                locationRepository.findRelatedContentAndArtists(locationId);

        // 연관 콘텐츠, 아티스트 ID 추출
        List<Long> contentIds = mappings.stream().map(m -> m.getContent().getId()).toList();
        List<Long> artistIds = mappings.stream().map(m -> m.getArtist().getId()).toList();

        // 배치 조회
        List<ContentResult> contents = contentQueryService.getContentsByIds(contentIds);
        List<ArtistResult> artists = artistQueryService.getArtistsByIds(artistIds);

        return LocationResult.of(location, imageResults, contents, artists);
    }

    private LocationResult.LocationImageResult toLocationImageResult(ImageLocation imageLocation) {
        ImageResult imageResult = imageQueryService.getImage(imageLocation.getImage().getId());
        return LocationResult.LocationImageResult.of(
                imageResult, imageLocation.getIsMain(), imageLocation.getDisplayOrder());
    }

    // 내 주변/지도범위 내 관광지 리스트 조회
    public List<LocationNearbyResult> getNearbyLocations(
            double lat, double lng, double radiusMeter) {
        GeoLocation userLocation = GeoLocation.of(lat, lng);
        List<Location> locations =
                locationRepository.findNearbyLocations(userLocation, radiusMeter);

        return locations.stream()
                .map(
                        location ->
                                LocationNearbyResult.of(
                                        location,
                                        userLocation.calculateDistanceMeterTo(
                                                location.getGeoLocation())))
                .collect(Collectors.toList());
    }

    // 관광지 관련 데이터(콘텐츠-아티스트) 조회 -> 배치 조회 (N+1 개선)
    public LocationRelatedInfoResult getRelatedContentAndArtists(Long locationId) {
        Location location =
                locationRepository
                        .findById(locationId)
                        .orElseThrow(
                                () -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));

        // 1. 매핑 데이터 가져오기.
        List<LocationContentArtist> mappings =
                locationRepository.findRelatedContentAndArtists(locationId);

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
                contents.stream().collect(Collectors.toMap(ContentResult::getId, c -> c));
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
                                            content.getId(),
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
