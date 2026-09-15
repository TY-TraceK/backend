package com.tracek.domain.ranking.infrastructure.persistence.nativequery;

import com.tracek.domain.ranking.domain.model.LocationRankingView;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import java.sql.Timestamp;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class LocationVisitRankingNativeRepository {

    private final EntityManager entityManager;

    public List<LocationRankingView> findTopRankings(RankingSearchCriteria<String> criteria) {

        String sql =
                """
            SELECT
                ranked.ranking,
                ranked.city,
                ranked.total_verification_count,
                ranked.last_update_at
            FROM (
                SELECT
                    grouped.city,
                    grouped.total_verification_count,
                    grouped.last_update_at,
                    RANK() OVER (
                        ORDER BY grouped.total_verification_count DESC
                    ) AS ranking
                FROM (
                    SELECT
                        l.city,
                        SUM(lr.total_visit_verification_count) AS total_verification_count,
                        MAX(lr.updated_at) AS last_update_at
                    FROM location_ranking lr
                    JOIN location l
                        ON l.id = lr.location_id
                    GROUP BY l.city
                ) grouped
            ) ranked
            WHERE ranked.ranking <= :limit
            ORDER BY
                ranked.total_verification_count DESC,
                ranked.city ASC
            """;

        Query query = entityManager.createNativeQuery(sql).setParameter("limit", criteria.limit());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        return rows.stream()
                .map(
                        row ->
                                new LocationRankingView(
                                        ((Number) row[0]).intValue(), // rank
                                        null, // locationId
                                        (String) row[1], // cityName
                                        null, // locationAddress
                                        row[3] != null
                                                ? ((Timestamp) row[3]).toLocalDateTime()
                                                : null, // lastUpdateAt
                                        ((Number) row[2]).longValue() // totalVerificationCount
                                        ))
                .toList();
    }
}
