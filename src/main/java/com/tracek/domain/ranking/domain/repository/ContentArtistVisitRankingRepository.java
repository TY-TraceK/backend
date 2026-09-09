package com.tracek.domain.ranking.domain.repository;

import com.tracek.domain.ranking.domain.model.ContentArtistVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import java.util.List;

public interface ContentArtistVisitRankingRepository {

    ContentArtistVisitRanking save(ContentArtistVisitRanking contentArtistVisitRanking);

    void increaseVerificationCount(Long contentId, Long artistId);

    void decreaseVerificationCount(Long contentId, Long artistId);

    List<RankingItem> findArtistsByContent(Long contentId, RankingSearchCriteria criteria);

    List<RankingItem> findContentsByArtist(Long artistId, RankingSearchCriteria criteria);
}
