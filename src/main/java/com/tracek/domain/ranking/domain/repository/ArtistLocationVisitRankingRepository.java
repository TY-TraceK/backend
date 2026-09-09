package com.tracek.domain.ranking.domain.repository;

import com.tracek.domain.ranking.domain.model.ArtistLocationVisitRanking;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ArtistLocationVisitRankingRepository {

    Optional<ArtistLocationVisitRanking> findByLocationIdAndArtistId(
            Long locationId, Long artistId);

    ArtistLocationVisitRanking save(ArtistLocationVisitRanking artistLocationVisitRanking);

    void increaseVerificationCount(Long locationId, Long artistId);

    void decreaseVerificationCount(Long locationId, Long artistId);
}
