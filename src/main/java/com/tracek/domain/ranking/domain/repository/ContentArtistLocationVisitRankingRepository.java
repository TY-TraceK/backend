package com.tracek.domain.ranking.domain.repository;

import com.tracek.domain.ranking.domain.model.ContentLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import com.tracek.domain.ranking.domain.model.TargetId;
import java.util.List;
import java.util.Optional;

public interface ContentArtistLocationVisitRankingRepository {

    Optional<ContentLocationVisitRanking> findByLocationIdAndContentId(
            Long locationId, Long contentId);

    ContentLocationVisitRanking save(ContentLocationVisitRanking contentLocationVisitRanking);

    void increaseVerificationCount(TargetId targetId);

    void decreaseVerificationCount(TargetId targetId);

    List<RankingItem> findRankingsByContent(Long contentId, RankingSearchCriteria<Long> criteria);

    List<RankingItem> findRankingsByLocation(Long locationId, RankingSearchCriteria<Long> criteria);

    List<RankingItem> findRankingsByArtist(Long artistId, RankingSearchCriteria<Long> criteria);
}
