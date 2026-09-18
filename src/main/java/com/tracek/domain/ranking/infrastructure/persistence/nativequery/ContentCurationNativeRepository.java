package com.tracek.domain.ranking.infrastructure.persistence.nativequery;

import com.tracek.domain.ranking.domain.model.ContentCurationView;
import jakarta.persistence.EntityManager;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentCurationNativeRepository {

    private static final int LOCATION_LIMIT = 3;

    private final EntityManager entityManager;

    public Optional<ContentCurationView> findLowVisitContentCuration() {
        @SuppressWarnings("unchecked")
        List<Object[]> contents =
                entityManager
                        .createNativeQuery(
                                """
                                SELECT
                                    c.id,
                                    c.title,
                                    SUM(r.total_visit_verification_count) AS total_verification_count
                                FROM content_location_ranking r
                                JOIN content c ON c.id = r.content_id
                                GROUP BY c.id, c.title
                                ORDER BY total_verification_count ASC, c.id ASC
                                """)
                        .setMaxResults(1)
                        .getResultList();

        if (contents.isEmpty()) {
            return Optional.empty();
        }

        Object[] content = contents.getFirst();
        Long contentId = ((Number) content[0]).longValue();

        @SuppressWarnings("unchecked")
        List<Object[]> locations =
                entityManager
                        .createNativeQuery(
                                """
                                SELECT
                                    l.id,
                                    l.name
                                FROM content_location_ranking r
                                JOIN location l ON l.id = r.location_id
                                WHERE r.content_id = :contentId
                                ORDER BY r.total_visit_verification_count DESC, l.id ASC
                                """)
                        .setParameter("contentId", contentId)
                        .setMaxResults(LOCATION_LIMIT)
                        .getResultList();

        return Optional.of(
                new ContentCurationView(
                        contentId,
                        (String) content[1],
                        ((Number) content[2]).longValue(),
                        locations.stream().map(row -> (String) row[1]).toList()));
    }
}
