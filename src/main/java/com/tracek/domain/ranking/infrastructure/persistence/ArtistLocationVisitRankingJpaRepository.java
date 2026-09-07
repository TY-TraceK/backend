package com.tracek.domain.ranking.infrastructure.persistence;

import com.tracek.domain.ranking.domain.model.ArtistLocationVisitRanking;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistLocationVisitRankingJpaRepository
        extends JpaRepository<ArtistLocationVisitRanking, Long> {

    Optional<ArtistLocationVisitRanking> findByLocationIdAndArtistId(
            Long locationId, Long artistId);
}
