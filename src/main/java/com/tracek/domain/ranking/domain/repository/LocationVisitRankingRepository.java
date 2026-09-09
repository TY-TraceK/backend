package com.tracek.domain.ranking.domain.repository;

import com.tracek.domain.ranking.domain.model.LocationVisitRanking;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationVisitRankingRepository {

    Optional<LocationVisitRanking> findByLocationId(Long locationId);

    LocationVisitRanking save(LocationVisitRanking locationVisitRanking);
}
