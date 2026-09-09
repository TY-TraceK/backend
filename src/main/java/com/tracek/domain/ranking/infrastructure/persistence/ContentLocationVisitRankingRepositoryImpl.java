package com.tracek.domain.ranking.infrastructure.persistence;

import com.tracek.domain.ranking.domain.model.ContentLocationVisitRanking;
import com.tracek.domain.ranking.domain.repository.ContentLocationVisitRankingRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentLocationVisitRankingRepositoryImpl
        implements ContentLocationVisitRankingRepository {

    private final ContentLocationVisitRankingJpaRepository contentLocationVisitRankingJpaRepository;

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
}
