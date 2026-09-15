package com.tracek.domain.ranking.domain.repository;

import com.tracek.domain.ranking.domain.model.LocationRankingView;
import com.tracek.domain.ranking.domain.model.LocationVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationVisitRankingRepository {

    Optional<LocationVisitRanking> findByLocationId(Long locationId);

    LocationVisitRanking save(LocationVisitRanking locationVisitRanking);

    void increaseVerificationCount(Long locationId);

    void decreaseVerificationCount(Long locationId);

    List<LocationRankingView> findLocationRankingsByRegion(RankingSearchCriteria<String> criteria);
}
