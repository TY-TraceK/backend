package com.tracek.domain.location.infrastructure.persistence;

import com.tracek.domain.location.domain.model.GeoLocation;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationContentArtist;
import com.tracek.domain.location.domain.repository.LocationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LocationRepositoryImpl implements LocationRepository {

    private final LocationJpaRepository locationJpaRepository;
    private final LocationContentArtistJpaRepository locationContentArtistJpaRepository;

    @Override
    public Location save(Location location) {
        return locationJpaRepository.save(location);
    }

    @Override
    public Optional<Location> findById(Long id) {
        return locationJpaRepository.findById(id);
    }

    @Override
    public List<Location> findAll() {
        return locationJpaRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        locationJpaRepository.deleteById(id);
    }

    @Override
    public List<Location> findNearbyLocations(GeoLocation userLocation, double radiusMeter) {
        return locationJpaRepository.findNearbyLocations(userLocation, radiusMeter);
    }

    @Override
    public List<LocationContentArtist> findRelatedContentAndArtists(Long locationId) {
        return locationContentArtistJpaRepository.findByLocationId(locationId);
    }
}
