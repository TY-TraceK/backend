package com.tracek.domain.ranking.domain.repository;

import com.tracek.domain.ranking.domain.model.ContentArtistVisitRanking;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentArtistVisitRankingRepository {

    ContentArtistVisitRanking save(ContentArtistVisitRanking contentArtistVisitRanking);

    void increaseVerificationCount(Long contentId, Long artistId);

    void decreaseVerificationCount(Long contentId, Long artistId);
}
