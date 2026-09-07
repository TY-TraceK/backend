package com.tracek.domain.ranking.infrastructure.persistence;

import com.tracek.domain.ranking.domain.model.LocationVisitRanking;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationVisitRankingJpaRepository extends
    JpaRepository<LocationVisitRanking, Long> {

  Optional<LocationVisitRanking> findByLocationId(Long locationId);
}