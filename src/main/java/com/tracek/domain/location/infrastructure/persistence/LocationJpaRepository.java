package com.tracek.domain.location.infrastructure.persistence;

import com.tracek.domain.location.domain.model.GeoLocation;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationCategory;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationJpaRepository extends JpaRepository<Location, Long> {

    // 내 주변/지도 반경 내 관광지 목록 조회
    // Native Query로 위치 기반 반경 조회 해결
    @Query(
            value =
                    "SELECT * FROM location l "
                            + "WHERE (6371000 * acos(cos(radians(:#{#userLocation.latitude})) * cos(radians(l.latitude)) "
                            + "* cos(radians(l.longitude) - radians(:#{#userLocation.longitude})) + sin(radians(:#{#userLocation.latitude})) "
                            + "* sin(radians(l.latitude)))) <= :radiusMeter",
            nativeQuery = true)
    List<Location> findNearbyLocations(GeoLocation userLocation, double radiusMeter);

    Page<Location> findByCategory(LocationCategory category, Pageable pageable);

    // 비관적 락을 걸고 장소 조회
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT l FROM Location l WHERE l.id = :id")
    Optional<Location> findByIdForUpdate(@Param("id") Long id);
}
