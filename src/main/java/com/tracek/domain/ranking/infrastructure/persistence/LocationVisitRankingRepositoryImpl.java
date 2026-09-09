package com.tracek.domain.ranking.infrastructure.persistence;

import com.tracek.domain.ranking.domain.model.LocationVisitRanking;
import com.tracek.domain.ranking.domain.repository.LocationVisitRankingRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LocationVisitRankingRepositoryImpl implements LocationVisitRankingRepository {

    private final LocationVisitRankingJpaRepository locationVisitRankingJpaRepository;

    @Override
    public Optional<LocationVisitRanking> findByLocationId(Long locationId) {
        return locationVisitRankingJpaRepository.findByLocationId(locationId);
    }

    @Override
    public LocationVisitRanking save(LocationVisitRanking locationVisitRanking) {
        return locationVisitRankingJpaRepository.save(locationVisitRanking);
    }
}
