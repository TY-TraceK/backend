package com.tracek.domain.ranking.domain.repository;

import com.tracek.domain.ranking.domain.model.ContentLocationVisitRanking;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentLocationVisitRankingRepository {

    Optional<ContentLocationVisitRanking> findByLocationIdAndContentId(
            Long locationId, Long contentId);

    ContentLocationVisitRanking save(ContentLocationVisitRanking contentLocationVisitRanking);

    void increaseVerificationCount(Long locationId, Long contentId);

    void decreaseVerificationCount(Long locationId, Long contentId);
}
