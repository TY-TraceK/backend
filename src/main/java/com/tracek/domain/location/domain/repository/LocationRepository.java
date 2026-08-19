package com.tracek.domain.location.domain.repository;

import com.tracek.domain.location.domain.model.*;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LocationRepository {
    Location save(Location location);

    Optional<Location> findById(Long id);

    List<Location> findAllByIds(List<Long> ids);

    List<Location> findAll();

    void deleteById(Long id);

    // 내 주변/지도 반경 내 관광지 목록 조회
    List<Location> findNearbyLocations(GeoLocation userLocation, double radiusMeter);

    // 해당 관광지와 연관된 콘텐츠-아티스트 매핑 조회
    List<LocationContentArtist> findRelatedContentAndArtists(Long locationId);

    // 해당 콘텐츠와 연관된 관광지-아티스트 매핑 조회
    List<LocationContentArtist> findRelatedLocationAndArtists(Long contentId);

    // 해당 아티스트와 연관된 관광지-콘텐츠 매핑 조회
    List<LocationContentArtist> findRelatedLocationAndContents(Long artistId);

    // 특정 카테고리의 관광지 목록 페이징 조회
    Page<Location> findByCategory(LocationCategory category, Pageable pageable);

    Page<Location> findAll(Pageable pageable);

    // 좋아요 존재 여부 확인
    boolean existsByUserIdAndLocationId(Long userId, Long locationId);

    Optional<LocationLike> findByUserIdAndLocationId(Long userId, Long locationId);

    void deleteByUserIdAndLocationId(Long userId, Long locationId);

    void saveLike(LocationLike locationLike);

    Optional<LocationContentArtist> findMappingById(Long contentArtistLocationId);

    // 비관적 락
    Optional<Location> findByIdForUpdate(Long id);

    // 낙관적 락
    Optional<Location> findByIdWithOptimisticLock(Long id);
}
