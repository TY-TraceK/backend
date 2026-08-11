package com.tracek.domain.location.infrastructure.persistence;

import com.tracek.domain.location.domain.model.LocationLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationLikeJpaRepository extends JpaRepository<LocationLike, Long> {
    boolean existsByUserIdAndLocationId(Long userId, Long locationId);

    Optional<LocationLike> findByUserIdAndLocationId(Long userId, Long locationId);

    void deleteByUserIdAndLocationId(Long userId, Long locationId);
}
