package com.tracek.domain.ranking.infrastructure.persistence.impl;

import com.tracek.domain.ranking.domain.model.ContentLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import com.tracek.domain.ranking.domain.repository.ContentLocationVisitRankingRepository;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.ContentLocationVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.qsdl.ContentLocationVisitRankingQueryDslRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentLocationVisitRankingRepositoryImpl
        implements ContentLocationVisitRankingRepository {

    private final ContentLocationVisitRankingJpaRepository contentLocationVisitRankingJpaRepository;
    private final ContentLocationVisitRankingQueryDslRepository
            contentLocationVisitRankingQueryDslRepository;

    @Override
    public Optional<ContentLocationVisitRanking> findByLocationIdAndContentId(
            Long locationId, Long contentId) {
        return contentLocationVisitRankingJpaRepository.findByLocationIdAndContentId(
                locationId, contentId);
    }

    @Override
    public ContentLocationVisitRanking save(
            ContentLocationVisitRanking contentLocationVisitRanking) {
        return contentLocationVisitRankingJpaRepository.save(contentLocationVisitRanking);
    }

    @Override
    public void increaseVerificationCount(Long locationId, Long contentId) {
        contentLocationVisitRankingJpaRepository.increaseVerificationCount(locationId, contentId);
    }

    @Override
    public void decreaseVerificationCount(Long locationId, Long contentId) {
        contentLocationVisitRankingJpaRepository.decreaseVerificationCount(locationId, contentId);
    }

    @Override
    public List<RankingItem> findLocationsByContent(
            Long contentId, RankingSearchCriteria<Long> criteria) {
        return contentLocationVisitRankingQueryDslRepository.findLocationsByContent(
                contentId, criteria);
    }

    @Override
    public List<RankingItem> findContentsByLocation(
            Long locationId, RankingSearchCriteria<Long> criteria) {
        return contentLocationVisitRankingQueryDslRepository.findContentsByLocation(
                locationId, criteria);
    }
}
