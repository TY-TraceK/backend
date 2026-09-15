package com.tracek.domain.ranking.infrastructure.persistence.impl;

import com.tracek.domain.ranking.domain.model.LocationRankingView;
import com.tracek.domain.ranking.domain.model.LocationVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import com.tracek.domain.ranking.domain.repository.LocationVisitRankingRepository;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.LocationVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.nativequery.LocationVisitRankingNativeRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LocationVisitRankingRepositoryImpl implements LocationVisitRankingRepository {

    private final LocationVisitRankingJpaRepository locationVisitRankingJpaRepository;
    private final LocationVisitRankingNativeRepository locationVisitRankingNativeRepository;

    @Override
    public Optional<LocationVisitRanking> findByLocationId(Long locationId) {
        return locationVisitRankingJpaRepository.findByLocationId(locationId);
    }

    @Override
    public LocationVisitRanking save(LocationVisitRanking locationVisitRanking) {
        return locationVisitRankingJpaRepository.save(locationVisitRanking);
    }

    @Override
    public void increaseVerificationCount(Long locationId) {
        locationVisitRankingJpaRepository.increaseVerificationCount(locationId);
    }

    @Override
    public void decreaseVerificationCount(Long locationId) {
        locationVisitRankingJpaRepository.decreaseVerificationCount(locationId);
    }

    @Override
    public List<LocationRankingView> findLocationRankingsByRegion(
            RankingSearchCriteria<String> criteria) {
        return locationVisitRankingNativeRepository.findTopRankings(criteria);
    }
}
