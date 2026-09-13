package com.tracek.domain.ranking.application.service.impl;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tracek.domain.ranking.domain.model.TargetId;
import com.tracek.domain.ranking.domain.repository.ArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.LocationVisitRankingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class VisitRankingProjectionServiceImplTest {

    @Mock private LocationVisitRankingRepository locationVisitRankingRepository;

    @Mock private ArtistLocationVisitRankingRepository artistLocationVisitRankingRepository;

    @Mock private ContentLocationVisitRankingRepository contentLocationVisitRankingRepository;

    @Mock private ContentArtistVisitRankingRepository contentArtistVisitRankingRepository;

    @Mock
    private ContentArtistLocationVisitRankingRepository contentArtistLocationVisitRankingRepository;

    private VisitRankingProjectionServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service =
                new VisitRankingProjectionServiceImpl(
                        locationVisitRankingRepository,
                        artistLocationVisitRankingRepository,
                        contentLocationVisitRankingRepository,
                        contentArtistVisitRankingRepository,
                        contentArtistLocationVisitRankingRepository);
    }

    @Test
    @DisplayName("관광지 방문 인증만 존재하면 관광지 랭킹만 증가한다")
    void increaseLocationOnly() {
        // when
        service.increase(1L, null, null);

        // then
        verify(locationVisitRankingRepository).increaseVerificationCount(1L);

        verify(artistLocationVisitRankingRepository, never()).increaseVerificationCount(1L, null);

        verify(contentLocationVisitRankingRepository, never()).increaseVerificationCount(1L, null);
    }

    @Test
    @DisplayName("아티스트가 존재하면 관광지-아티스트 랭킹도 증가한다")
    void increaseWithArtist() {
        service.increase(1L, null, 2L);

        verify(locationVisitRankingRepository).increaseVerificationCount(1L);

        verify(artistLocationVisitRankingRepository).increaseVerificationCount(1L, 2L);

        verify(contentArtistVisitRankingRepository, never()).increaseVerificationCount(2L, 2L);
    }

    @Test
    @DisplayName("콘텐츠와 아티스트가 모두 존재하면 모든 관련 랭킹을 증가한다")
    void increaseAll() {
        // given
        TargetId targetId = new TargetId(1L, 2L, 3L);

        // when
        service.increase(1L, 2L, 3L);

        // then
        verify(locationVisitRankingRepository).increaseVerificationCount(1L);

        verify(artistLocationVisitRankingRepository).increaseVerificationCount(1L, 3L);

        verify(contentLocationVisitRankingRepository).increaseVerificationCount(1L, 2L);

        verify(contentArtistVisitRankingRepository).increaseVerificationCount(2L, 3L);

        verify(contentArtistLocationVisitRankingRepository).increaseVerificationCount(targetId);
    }

    @Test
    @DisplayName("관광지 방문 인증만 취소하면 관광지 랭킹만 감소한다")
    void decreaseLocationOnly() {
        service.decrease(1L, null, null);

        verify(locationVisitRankingRepository).decreaseVerificationCount(1L);

        verify(artistLocationVisitRankingRepository, never()).decreaseVerificationCount(1L, null);

        verify(contentLocationVisitRankingRepository, never()).decreaseVerificationCount(1L, null);
    }

    @Test
    @DisplayName("아티스트가 존재하면 관광지-아티스트 랭킹도 감소한다")
    void decreaseWithArtist() {
        service.decrease(1L, null, 2L);

        verify(locationVisitRankingRepository).decreaseVerificationCount(1L);

        verify(artistLocationVisitRankingRepository).decreaseVerificationCount(1L, 2L);
    }

    @Test
    @DisplayName("콘텐츠와 아티스트가 모두 존재하면 모든 관련 랭킹을 감소한다")
    void decreaseAll() {
        // given
        TargetId targetId = new TargetId(1L, 2L, 3L);

        // when
        service.decrease(1L, 2L, 3L);

        // then
        verify(locationVisitRankingRepository).decreaseVerificationCount(1L);

        verify(artistLocationVisitRankingRepository).decreaseVerificationCount(1L, 3L);

        verify(contentLocationVisitRankingRepository).decreaseVerificationCount(1L, 2L);

        verify(contentArtistVisitRankingRepository).decreaseVerificationCount(2L, 3L);

        verify(contentArtistLocationVisitRankingRepository).decreaseVerificationCount(targetId);
    }
}
