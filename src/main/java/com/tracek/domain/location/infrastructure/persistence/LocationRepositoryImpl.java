package com.tracek.domain.location.infrastructure.persistence;

import com.tracek.domain.location.domain.model.*;
import com.tracek.domain.location.domain.repository.LocationRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LocationRepositoryImpl implements LocationRepository {

    private final LocationJpaRepository locationJpaRepository;
    private final LocationContentArtistJpaRepository locationContentArtistJpaRepository;
    private final LocationLikeJpaRepository locationLikeJpaRepository;

    @Override
    public Location save(Location location) {
        return locationJpaRepository.save(location);
    }

    @Override
    public Optional<Location> findById(Long id) {
        return locationJpaRepository.findById(id);
    }

    @Override
    public List<Location> findAllByIds(List<Long> ids) {
        return locationJpaRepository.findAllById(ids);
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

    @Override
    public List<LocationContentArtist> findRelatedLocationAndArtists(Long contentId) {
        return locationContentArtistJpaRepository.findByContentId(contentId);
    }

    @Override
    public List<LocationContentArtist> findRelatedLocationAndContents(Long artistId) {
        return locationContentArtistJpaRepository.findByArtistId(artistId);
    }

    @Override
    public Page<Location> findByCategory(LocationCategory category, Pageable pageable) {
        return locationJpaRepository.findByCategory(category, pageable);
    }

    @Override
    public Page<Location> findAll(Pageable pageable) {
        return locationJpaRepository.findAll(pageable);
    }

    @Override
    public boolean existsByUserIdAndLocationId(Long userId, Long locationId) {
        return locationLikeJpaRepository.existsByUserIdAndLocationId(userId, locationId);
    }

    @Override
    public Optional<LocationLike> findByUserIdAndLocationId(Long userId, Long locationId) {
        return locationLikeJpaRepository.findByUserIdAndLocationId(userId, locationId);
    }

    @Override
    public void deleteByUserIdAndLocationId(Long userId, Long locationId) {
        locationLikeJpaRepository.deleteByUserIdAndLocationId(userId, locationId);
    }

    @Override
    public void saveLike(LocationLike locationLike) {
        locationLikeJpaRepository.save(locationLike);
    }

    @Override
    public Optional<LocationContentArtist> findMappingById(Long contentArtistLocationId) {
        return locationContentArtistJpaRepository.findById(contentArtistLocationId);
    }

    @Override
    public Optional<Location> findByIdForUpdate(Long id) {
        return locationJpaRepository.findByIdForUpdate(id);
    }
}
