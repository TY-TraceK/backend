package com.tracek.domain.ranking.infrastructure.persistence;

import com.tracek.domain.ranking.domain.model.ArtistLocationVisitRanking;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArtistLocationVisitRankingJpaRepository
        extends JpaRepository<ArtistLocationVisitRanking, Long> {

    Optional<ArtistLocationVisitRanking> findByLocationIdAndArtistId(
            Long locationId, Long artistId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
      UPDATE ArtistLocationVisitRanking r
         SET r.totalVerificationCount = r.totalVerificationCount + 1
       WHERE r.locationId = :locationId
         AND r.artistId = :artistId
      """)
    int increaseVerificationCount(
            @Param("locationId") Long locationId, @Param("artistId") Long artistId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
      UPDATE ArtistLocationVisitRanking r
         SET r.totalVerificationCount = r.totalVerificationCount - 1
       WHERE r.locationId = :locationId
         AND r.artistId = :artistId
         AND r.totalVerificationCount > 0
      """)
    int decreaseVerificationCount(
            @Param("locationId") Long locationId, @Param("artistId") Long artistId);
}
