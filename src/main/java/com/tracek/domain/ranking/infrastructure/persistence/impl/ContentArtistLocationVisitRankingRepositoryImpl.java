package com.tracek.domain.ranking.infrastructure.persistence.impl;

import com.tracek.domain.ranking.domain.model.ContentLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import com.tracek.domain.ranking.domain.model.TargetId;
import com.tracek.domain.ranking.domain.repository.ContentArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.ContentArtistLocationVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.ContentArtistVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.qsdl.ContentArtistLocationVisitRankingQueryDslRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentArtistLocationVisitRankingRepositoryImpl
        implements ContentArtistLocationVisitRankingRepository {

    private final ContentArtistLocationVisitRankingQueryDslRepository
            contentArtistLocationVisitRankingQueryDslRepository;
    private final ContentArtistVisitRankingJpaRepository contentArtistVisitRankingJpaRepository;
    private final ContentArtistLocationVisitRankingJpaRepository
            contentArtistLocationVisitRankingJpaRepository;

    @Override
    public Optional<ContentLocationVisitRanking> findByLocationIdAndContentId(
            Long locationId, Long contentId) {
        return Optional.empty();
    }

    @Override
    public ContentLocationVisitRanking save(
            ContentLocationVisitRanking contentLocationVisitRanking) {
        return null;
    }

    @Override
    public void increaseVerificationCount(TargetId targetId) {
        contentArtistLocationVisitRankingJpaRepository.increaseVerificationCount(
                targetId.locationId(), targetId.artistId(), targetId.contentId());
    }

    @Override
    public void decreaseVerificationCount(TargetId targetId) {
        contentArtistLocationVisitRankingJpaRepository.decreaseVerificationCount(
                targetId.locationId(), targetId.artistId(), targetId.contentId());
    }

    @Override
    public List<RankingItem> findRankingsByContent(
            Long contentId, RankingSearchCriteria<Long> criteria) {
        return contentArtistLocationVisitRankingQueryDslRepository.findRankingsByContent(
                contentId, criteria);
    }

    @Override
    public List<RankingItem> findRankingsByLocation(
            Long locationId, RankingSearchCriteria<Long> criteria) {
        return contentArtistLocationVisitRankingQueryDslRepository.findRankingsByLocation(
                locationId, criteria);
    }

    @Override
    public List<RankingItem> findRankingsByArtist(
            Long artistId, RankingSearchCriteria<Long> criteria) {
        return contentArtistLocationVisitRankingQueryDslRepository.findRankingsByArtist(
                artistId, criteria);
    }
}
