package com.tracek.domain.location.application.service;

import com.tracek.domain.location.application.dto.LocationNearbyResult;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.domain.location.domain.model.GeoLocation;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationContentArtist;
import com.tracek.domain.location.domain.repository.LocationRepository;
import com.tracek.global.exception.CustomException;
import java.util.Collections;
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

    // 관광지 Entity get
    public Location getLocationEntity(Long locationId) {
        return locationRepository
                .findById(locationId)
                .orElseThrow(() -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));
    }

    // 관광지 여러개 조회
    public List<LocationResult> getLocationByIds(List<Long> locationIds) {
        if (locationIds == null || locationIds.isEmpty()) {
            return Collections.emptyList();
        }
        return locationRepository.findAllByIds(locationIds).stream()
                .map(LocationResult::from)
                .toList();
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

    // 관광지 관련 데이터(콘텐츠-아티스트) 매핑 정보(LocationContentArtist 도메인) 조회 요청시
    public List<LocationContentArtist> getMappingsByLocationId(Long locationId) {
        return locationRepository.findRelatedContentAndArtists(locationId);
    }

    // 타 도메인에서 콘텐츠와 연관된 관광지-아티스트 매핑 정보(LocationContentArtist 도메인) 조회 요청시
    public List<LocationContentArtist> getMappingsByContentId(Long contentId) {
        return locationRepository.findRelatedLocationAndArtists(contentId);
    }

    // 타 도메인에서 아티스트와 연관된 관광지-콘텐츠 매핑 정보(LocationContentArtist 도메인) 조회 요청시
    public List<LocationContentArtist> getMappingByArtistId(Long artistId) {
        return locationRepository.findRelatedLocationAndContents(artistId);
    }
}
