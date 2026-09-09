package com.tracek.domain.ranking.infrastructure.persistence;

import com.tracek.domain.ranking.domain.model.LocationVisitRanking;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationVisitRankingJpaRepository
        extends JpaRepository<LocationVisitRanking, Long> {

    Optional<LocationVisitRanking> findByLocationId(Long locationId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
      UPDATE LocationVisitRanking r
         SET r.totalVerificationCount = r.totalVerificationCount + 1
       WHERE r.locationId = :locationId
      """)
    int increaseVerificationCount(@Param("locationId") Long locationId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
      UPDATE LocationVisitRanking r
         SET r.totalVerificationCount = r.totalVerificationCount - 1
       WHERE r.locationId = :locationId
         AND r.totalVerificationCount > 0
      """)
    int decreaseVerificationCount(@Param("locationId") Long locationId);
}
