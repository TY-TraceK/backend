package com.tracek.domain.ranking.infrastructure.persistence;

import com.tracek.domain.ranking.domain.model.ContentArtistVisitRanking;
import com.tracek.domain.ranking.domain.repository.ContentArtistVisitRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentArtistVisitVisitRankingRepositoryImpl
        implements ContentArtistVisitRankingRepository {

    private final ContentArtistVisitRankingJpaRepository contentArtistVisitRankingJpaRepository;

    @Override
    public ContentArtistVisitRanking save(ContentArtistVisitRanking contentArtistVisitRanking) {
        return contentArtistVisitRankingJpaRepository.save(contentArtistVisitRanking);
    }

    @Override
    public void increaseVerificationCount(Long contentId, Long artistId) {
        contentArtistVisitRankingJpaRepository.increaseVerificationCount(contentId, artistId);
    }

    @Override
    public void decreaseVerificationCount(Long contentId, Long artistId) {
        contentArtistVisitRankingJpaRepository.decreaseVerificationCount(contentId, artistId);
    }
}
