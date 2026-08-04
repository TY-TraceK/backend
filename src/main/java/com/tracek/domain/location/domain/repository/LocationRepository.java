package com.tracek.domain.location.domain.repository;

import com.tracek.domain.location.domain.model.GeoLocation;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationContentArtist;
import java.util.List;
import java.util.Optional;

public interface LocationRepository {
    Location save(Location location);

    Optional<Location> findById(Long id);

    List<Location> findAll();

    void deleteById(Long id);

    // 내 주변/지도 반경 내 관광지 목록 조회
    List<Location> findNearbyLocations(GeoLocation userLocation, double radiusMeter);

    // 해당 관광지와 연관된 콘텐츠-아티스트 매핑 조회
    List<LocationContentArtist> findRelatedContentAndArtists(Long locationId);
}
