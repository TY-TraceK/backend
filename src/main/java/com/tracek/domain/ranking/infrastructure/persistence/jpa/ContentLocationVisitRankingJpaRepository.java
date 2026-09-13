package com.tracek.domain.ranking.infrastructure.persistence.jpa;

import com.tracek.domain.ranking.domain.model.ContentLocationVisitRanking;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentLocationVisitRankingJpaRepository
        extends JpaRepository<ContentLocationVisitRanking, Long> {

    Optional<ContentLocationVisitRanking> findByLocationIdAndContentId(
            Long locationId, Long contentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
          UPDATE ContentLocationVisitRanking r
             SET r.totalVerificationCount = r.totalVerificationCount + 1
           WHERE r.locationId = :locationId
             AND r.contentId = :contentId
          """)
    int increaseVerificationCount(
            @Param("locationId") Long locationId, @Param("contentId") Long contentId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
          UPDATE ContentLocationVisitRanking r
             SET r.totalVerificationCount = r.totalVerificationCount - 1
           WHERE r.locationId = :locationId
             AND r.contentId = :contentId
             AND r.totalVerificationCount > 0
          """)
    int decreaseVerificationCount(
            @Param("locationId") Long locationId, @Param("contentId") Long contentId);
}
