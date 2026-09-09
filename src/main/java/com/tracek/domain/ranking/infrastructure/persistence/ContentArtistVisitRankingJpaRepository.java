package com.tracek.domain.ranking.infrastructure.persistence;

import com.tracek.domain.ranking.domain.model.ContentArtistVisitRanking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentArtistVisitRankingJpaRepository
        extends JpaRepository<ContentArtistVisitRanking, Long> {

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
      UPDATE ContentArtistVisitRanking r
         SET r.totalVerificationCount = r.totalVerificationCount + 1
       WHERE r.contentId = :contentId
         AND r.artistId = :artistId
      """)
    int increaseVerificationCount(
            @Param("contentId") Long contentId, @Param("artistId") Long artistId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            """
      UPDATE ContentArtistVisitRanking r
         SET r.totalVerificationCount = r.totalVerificationCount - 1
       WHERE r.contentId = :contentId
         AND r.artistId = :artistId
         AND r.totalVerificationCount > 0
      """)
    int decreaseVerificationCount(
            @Param("contentId") Long contentId, @Param("artistId") Long artistId);
}
