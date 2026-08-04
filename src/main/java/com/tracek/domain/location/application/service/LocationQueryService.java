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

        List<LocationResult.LocationImageResult> imageResults =
                location.getImageLocations().stream()
                        .map(this::toLocationImageResult)
                        .collect(Collectors.toList());

        return LocationResult.of(location, imageResults);
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

    public LocationRelatedInfoResult getRelatedContentAndArtists(Long locationId) {
        Location location =
                locationRepository
                        .findById(locationId)
                        .orElseThrow(
                                () -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));

        List<LocationContentArtist> mappings =
                locationRepository.findRelatedContentAndArtists(locationId);

        List<LocationRelatedInfoResult.RelatedItemResult> relatedItems =
                mappings.stream()
                        .map(
                                m -> {
                                    ContentResult content =
                                            contentQueryService.getContent(m.getContent().getId());
                                    ArtistResult artist =
                                            artistQueryService.getArtist(m.getArtist().getId());
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
