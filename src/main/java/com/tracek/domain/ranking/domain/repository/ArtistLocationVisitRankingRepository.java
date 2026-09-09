package com.tracek.domain.ranking.domain.repository;

import com.tracek.domain.ranking.domain.model.ArtistLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import java.util.List;
import java.util.Optional;

public interface ArtistLocationVisitRankingRepository {

    Optional<ArtistLocationVisitRanking> findByLocationIdAndArtistId(
            Long locationId, Long artistId);

    ArtistLocationVisitRanking save(ArtistLocationVisitRanking artistLocationVisitRanking);

    void increaseVerificationCount(Long locationId, Long artistId);

    void decreaseVerificationCount(Long locationId, Long artistId);

    List<RankingItem> findLocationsByArtist(Long artistId, RankingSearchCriteria criteria);

    List<RankingItem> findArtistsByLocation(Long locationId, RankingSearchCriteria criteria);
}
