package com.tracek.domain.ranking.domain.repository;

import com.tracek.domain.ranking.domain.model.ContentLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import java.util.List;
import java.util.Optional;

public interface ContentLocationVisitRankingRepository {

    Optional<ContentLocationVisitRanking> findByLocationIdAndContentId(
            Long locationId, Long contentId);

    ContentLocationVisitRanking save(ContentLocationVisitRanking contentLocationVisitRanking);

    void increaseVerificationCount(Long locationId, Long contentId);

    void decreaseVerificationCount(Long locationId, Long contentId);

    List<RankingItem> findLocationsByContent(Long contentId, RankingSearchCriteria criteria);

    List<RankingItem> findContentsByLocation(Long locationId, RankingSearchCriteria criteria);
}
