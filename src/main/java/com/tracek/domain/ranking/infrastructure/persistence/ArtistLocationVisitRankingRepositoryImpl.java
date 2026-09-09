package com.tracek.domain.ranking.infrastructure.persistence;

import com.tracek.domain.ranking.domain.model.ArtistLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import com.tracek.domain.ranking.domain.repository.ArtistLocationVisitRankingRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArtistLocationVisitRankingRepositoryImpl
        implements ArtistLocationVisitRankingRepository {

    private final ArtistLocationVisitRankingJpaRepository artistLocationVisitRankingJpaRepository;
    private final ArtistLocationVisitRankingQueryDslRepository
            artistLocationVisitRankingQueryDslRepository;

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

    @Override
    public List<RankingItem> findLocationsByArtist(Long artistId, RankingSearchCriteria criteria) {
        return artistLocationVisitRankingQueryDslRepository.findLocationsByArtist(
                artistId, criteria);
    }

    @Override
    public List<RankingItem> findArtistsByLocation(
            Long locationId, RankingSearchCriteria criteria) {
        return artistLocationVisitRankingQueryDslRepository.findArtistsByLocation(
                locationId, criteria);
    }
}
