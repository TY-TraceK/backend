package com.tracek.domain.ranking.infrastructure.persistence.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import com.tracek.domain.ranking.domain.model.TargetId;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.ArtistLocationVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.ContentArtistLocationVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.ContentArtistVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.ContentLocationVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.qsdl.ArtistLocationVisitRankingQueryDslRepository;
import com.tracek.domain.ranking.infrastructure.persistence.qsdl.ContentArtistLocationVisitRankingQueryDslRepository;
import com.tracek.domain.ranking.infrastructure.persistence.qsdl.ContentLocationVisitRankingQueryDslRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RankingRepositoryImplDelegationTest {

    @Mock ArtistLocationVisitRankingJpaRepository artistLocationJpaRepository;
    @Mock ArtistLocationVisitRankingQueryDslRepository artistLocationQueryRepository;
    @Mock ContentLocationVisitRankingJpaRepository contentLocationJpaRepository;
    @Mock ContentLocationVisitRankingQueryDslRepository contentLocationQueryRepository;
    @Mock ContentArtistLocationVisitRankingJpaRepository contentArtistLocationJpaRepository;
    @Mock ContentArtistVisitRankingJpaRepository contentArtistVisitJpaRepository;
    @Mock ContentArtistLocationVisitRankingQueryDslRepository contentArtistLocationQueryRepository;

    @Test
    void artistLocationRepositoryDelegatesAllOperations() {
        var repository =
                new ArtistLocationVisitRankingRepositoryImpl(
                        artistLocationJpaRepository, artistLocationQueryRepository);
        RankingSearchCriteria<Long> criteria =
                new RankingSearchCriteria<>(null, null, null, 10, null, null);

        assertThat(repository.findByLocationIdAndArtistId(1L, 2L)).isEmpty();
        assertThat(repository.save(null)).isNull();
        repository.increaseVerificationCount(1L, 2L);
        repository.decreaseVerificationCount(1L, 2L);
        assertThat(repository.findLocationsByArtist(2L, criteria)).isEmpty();
        assertThat(repository.findArtistsByLocation(1L, criteria)).isEmpty();

        verify(artistLocationJpaRepository).findByLocationIdAndArtistId(1L, 2L);
        verify(artistLocationJpaRepository).save(null);
        verify(artistLocationJpaRepository).increaseVerificationCount(1L, 2L);
        verify(artistLocationJpaRepository).decreaseVerificationCount(1L, 2L);
        verify(artistLocationQueryRepository).findLocationsByArtist(2L, criteria);
        verify(artistLocationQueryRepository).findArtistsByLocation(1L, criteria);
    }

    @Test
    void contentLocationRepositoryDelegatesAllOperations() {
        var repository =
                new ContentLocationVisitRankingRepositoryImpl(
                        contentLocationJpaRepository, contentLocationQueryRepository);
        RankingSearchCriteria<Long> criteria =
                new RankingSearchCriteria<>(null, null, null, 10, null, null);

        assertThat(repository.findByLocationIdAndContentId(1L, 3L)).isEmpty();
        assertThat(repository.save(null)).isNull();
        repository.increaseVerificationCount(1L, 3L);
        repository.decreaseVerificationCount(1L, 3L);
        assertThat(repository.findLocationsByContent(3L, criteria)).isEmpty();
        assertThat(repository.findContentsByLocation(1L, criteria)).isEmpty();

        verify(contentLocationJpaRepository).findByLocationIdAndContentId(1L, 3L);
        verify(contentLocationJpaRepository).save(null);
        verify(contentLocationJpaRepository).increaseVerificationCount(1L, 3L);
        verify(contentLocationJpaRepository).decreaseVerificationCount(1L, 3L);
        verify(contentLocationQueryRepository).findLocationsByContent(3L, criteria);
        verify(contentLocationQueryRepository).findContentsByLocation(1L, criteria);
    }

    @Test
    void contentArtistLocationRepositoryCoversStubAndDelegationOperations() {
        var repository =
                new ContentArtistLocationVisitRankingRepositoryImpl(
                        contentArtistLocationQueryRepository,
                        contentArtistVisitJpaRepository,
                        contentArtistLocationJpaRepository);
        RankingSearchCriteria<Long> criteria =
                new RankingSearchCriteria<>(null, null, null, 10, null, null);
        TargetId targetId = new TargetId(1L, 3L, 2L);

        assertThat(repository.findByLocationIdAndContentId(1L, 3L)).isEmpty();
        assertThat(repository.save(null)).isNull();
        repository.increaseVerificationCount(targetId);
        repository.decreaseVerificationCount(targetId);
        assertThat(repository.findRankingsByContent(3L, criteria)).isEmpty();
        assertThat(repository.findRankingsByLocation(1L, criteria)).isEmpty();
        assertThat(repository.findRankingsByArtist(2L, criteria)).isEmpty();

        verify(contentArtistLocationJpaRepository).increaseVerificationCount(1L, 2L, 3L);
        verify(contentArtistLocationJpaRepository).decreaseVerificationCount(1L, 2L, 3L);
        verify(contentArtistLocationQueryRepository).findRankingsByContent(3L, criteria);
        verify(contentArtistLocationQueryRepository).findRankingsByLocation(1L, criteria);
        verify(contentArtistLocationQueryRepository).findRankingsByArtist(2L, criteria);
    }
}
