package com.tracek.domain.ranking.infrastructure.persistence;

import com.tracek.domain.ranking.domain.model.ArtistLocationVisitRanking;
import com.tracek.domain.ranking.domain.repository.ArtistLocationVisitRankingRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArtistLocationVisitRankingRepositoryImpl
        implements ArtistLocationVisitRankingRepository {

    private final ArtistLocationVisitRankingJpaRepository artistLocationVisitRankingJpaRepository;

    @Override
    public Optional<ArtistLocationVisitRanking> findByLocationIdAndArtistId(
            Long locationId, Long artistId) {
        return artistLocationVisitRankingJpaRepository.findByLocationIdAndArtistId(
                locationId, artistId);
    }

    @Override
    public ArtistLocationVisitRanking save(ArtistLocationVisitRanking artistLocationVisitRanking) {
        return artistLocationVisitRankingJpaRepository.save(artistLocationVisitRanking);
    }

    @Override
    public void increaseVerificationCount(Long locationId, Long artistId) {
        artistLocationVisitRankingJpaRepository.increaseVerificationCount(locationId, artistId);
    }

    @Override
    public void decreaseVerificationCount(Long locationId, Long artistId) {
        artistLocationVisitRankingJpaRepository.decreaseVerificationCount(locationId, artistId);
    }
}
