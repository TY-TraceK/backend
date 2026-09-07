package com.tracek.domain.ranking.infrastructure.persistence;

import com.tracek.domain.ranking.domain.model.ContentLocationVisitRanking;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentLocationVisitRankingJpaRepository
        extends JpaRepository<ContentLocationVisitRanking, Long> {

    Optional<ContentLocationVisitRanking> findByLocationIdAndContentId(
            Long locationId, Long contentId);
}
