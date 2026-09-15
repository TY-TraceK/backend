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

    public List<LocationRankingView> findRegionTopRankings(RankingSearchCriteria<String> criteria) {

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
                                LocationRankingView.builder()
                                        .rank(((Number) row[0]).intValue())
                                        .cityName((String) row[1])
                                        .lastUpdateAt(
                                                row[3] != null
                                                        ? ((Timestamp) row[3]).toLocalDateTime()
                                                        : null)
                                        .totalVerificationCount(((Number) row[2]).longValue())
                                        .build())
                .toList();
    }

    public List<LocationRankingView> findLocationTopRankings(RankingSearchCriteria<Long> criteria) {

        String sql =
                """
            SELECT
                ranked.id,
                ranked.ranking,
                ranked.city,
                ranked.locationName,
                ranked.total_visit_verification_count,
                ranked.updated_at,
                ranked.image_url
            FROM (
                SELECT
                    l.id,
                    l.city,
                    l.image_url,
                    l.name as locationName,
                    r.total_visit_verification_count,
                    r.updated_at,
                    RANK() OVER (
                        ORDER BY r.total_visit_verification_count DESC
                    ) AS ranking
                FROM location_ranking r join location l on r.location_id = l.id
                WHERE (:category IS NULL OR l.category = :category) AND (:city IS NULL OR l.city = :city)
            ) ranked
            WHERE ranked.ranking <= :limit
            ORDER BY
                ranked.total_visit_verification_count DESC,
                ranked.id
            """;

        Query query =
                entityManager
                        .createNativeQuery(sql)
                        .setParameter("limit", criteria.limit())
                        .setParameter("category", criteria.categoryName())
                        .setParameter("city", criteria.cityName());

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.getResultList();

        return rows.stream()
                .map(
                        row ->
                                LocationRankingView.builder()
                                        .rank(((Number) row[1]).intValue())
                                        .locationId(((Number) row[0]).longValue())
                                        .locationName((String) row[3])
                                        .cityName((String) row[2])
                                        .lastUpdateAt(
                                                row[5] != null
                                                        ? ((Timestamp) row[5]).toLocalDateTime()
                                                        : null)
                                        .totalVerificationCount(((Number) row[4]).longValue())
                                        .imageUrl((String) row[6])
                                        .build())
                .toList();
    }
}
