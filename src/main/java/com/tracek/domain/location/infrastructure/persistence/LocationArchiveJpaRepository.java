package com.tracek.domain.location.infrastructure.persistence;

import com.tracek.domain.location.domain.model.LocationArchive;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationArchiveJpaRepository extends JpaRepository<LocationArchive, Long> {
    boolean existsByUserIdAndLocationId(Long userId, Long locationId);

    Optional<LocationArchive> findByUserIdAndLocationId(Long userId, Long locationId);

    void deleteByUserIdAndLocationId(Long userId, Long locationId);

    List<LocationArchive> findAllByUserIdOrderByCreatedAtDesc(Long userId);
}
