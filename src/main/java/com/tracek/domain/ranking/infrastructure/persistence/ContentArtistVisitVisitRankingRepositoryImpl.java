package com.tracek.domain.ranking.infrastructure.persistence;

import com.tracek.domain.ranking.domain.model.ContentArtistVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import com.tracek.domain.ranking.domain.repository.ContentArtistVisitRankingRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentArtistVisitVisitRankingRepositoryImpl
        implements ContentArtistVisitRankingRepository {

    private final ContentArtistVisitRankingJpaRepository contentArtistVisitRankingJpaRepository;
    private final ContentArtistVisitRankingQueryDslRepository
            contentArtistVisitRankingQueryDslRepository;

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

    @Override
    public List<RankingItem> findArtistsByContent(Long contentId, RankingSearchCriteria criteria) {
        return contentArtistVisitRankingQueryDslRepository.findArtistsByContent(
                contentId, criteria);
    }

    @Override
    public List<RankingItem> findContentsByArtist(Long artistId, RankingSearchCriteria criteria) {
        return contentArtistVisitRankingQueryDslRepository.findContentsByArtist(artistId, criteria);
    }
}
