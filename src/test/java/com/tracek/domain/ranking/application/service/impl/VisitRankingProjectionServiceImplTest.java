package com.tracek.domain.ranking.application.service.impl;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.tracek.domain.ranking.domain.model.TargetId;
import com.tracek.domain.ranking.domain.repository.ArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.LocationVisitRankingRepository;
import java.util.Set;
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
    @DisplayName("방문 인증 생성 시 모든 관련 랭킹을 증가한다")
    void increase() {
        // given
        Long locationId = 1L;
        Long contentId = 2L;
        Long artistId = 3L;

        TargetId targetId = new TargetId(locationId, contentId, artistId);

        // when
        service.increase(locationId, contentId, Set.of(artistId));

        // then
        verify(locationVisitRankingRepository).increaseVerificationCount(locationId);

        verify(artistLocationVisitRankingRepository)
                .increaseVerificationCount(locationId, artistId);

        verify(contentLocationVisitRankingRepository)
                .increaseVerificationCount(locationId, contentId);

        verify(contentArtistVisitRankingRepository).increaseVerificationCount(contentId, artistId);

        verify(contentArtistLocationVisitRankingRepository).increaseVerificationCount(targetId);
    }

    @Test
    @DisplayName("장소 단독 방문 인증은 로케이션 랭킹만 증가한다")
    void increaseLocationOnly() {
        service.increase(1L, null, Set.of());

        verify(locationVisitRankingRepository).increaseVerificationCount(1L);
        verifyNoInteractions(
                artistLocationVisitRankingRepository,
                contentLocationVisitRankingRepository,
                contentArtistVisitRankingRepository,
                contentArtistLocationVisitRankingRepository);
    }

    @Test
    @DisplayName("장소 단독 방문 인증 취소는 로케이션 랭킹만 감소한다")
    void decreaseLocationOnly() {
        service.decrease(1L, null, Set.of());

        verify(locationVisitRankingRepository).decreaseVerificationCount(1L);
        verifyNoInteractions(
                artistLocationVisitRankingRepository,
                contentLocationVisitRankingRepository,
                contentArtistVisitRankingRepository,
                contentArtistLocationVisitRankingRepository);
    }

    @Test
    @DisplayName("방문 인증 취소 시 모든 관련 랭킹을 감소한다")
    void decrease() {
        // given
        Long locationId = 1L;
        Long contentId = 2L;
        Long artistId = 3L;

        TargetId targetId = new TargetId(locationId, contentId, artistId);

        // when
        service.decrease(locationId, contentId, Set.of(artistId));

        // then
        verify(locationVisitRankingRepository).decreaseVerificationCount(locationId);

        verify(artistLocationVisitRankingRepository)
                .decreaseVerificationCount(locationId, artistId);

        verify(contentLocationVisitRankingRepository)
                .decreaseVerificationCount(locationId, contentId);

        verify(contentArtistVisitRankingRepository).decreaseVerificationCount(contentId, artistId);

        verify(contentArtistLocationVisitRankingRepository).decreaseVerificationCount(targetId);
    }

    @Test
    @DisplayName("콘텐츠만 변경되면 콘텐츠 관련 랭킹만 변경한다")
    void updateContentOnly() {
        // given
        Long locationId = 1L;

        Long previousContentId = 2L;
        Long previousArtistId = 3L;

        Long updatedContentId = 4L;
        Long updatedArtistId = 3L;

        TargetId previousTarget = new TargetId(locationId, previousContentId, previousArtistId);

        TargetId updatedTarget = new TargetId(locationId, updatedContentId, updatedArtistId);

        // when
        service.update(
                locationId,
                previousContentId,
                Set.of(previousArtistId),
                updatedContentId,
                Set.of(updatedArtistId));

        // then
        verifyNoInteractions(locationVisitRankingRepository);
        verifyNoInteractions(artistLocationVisitRankingRepository);

        verify(contentLocationVisitRankingRepository)
                .decreaseVerificationCount(locationId, previousContentId);

        verify(contentLocationVisitRankingRepository)
                .increaseVerificationCount(locationId, updatedContentId);

        verify(contentArtistVisitRankingRepository)
                .decreaseVerificationCount(previousContentId, previousArtistId);

        verify(contentArtistVisitRankingRepository)
                .increaseVerificationCount(updatedContentId, updatedArtistId);

        verify(contentArtistLocationVisitRankingRepository)
                .decreaseVerificationCount(previousTarget);

        verify(contentArtistLocationVisitRankingRepository)
                .increaseVerificationCount(updatedTarget);
    }

    @Test
    @DisplayName("아티스트만 변경되면 아티스트 관련 랭킹만 변경한다")
    void updateArtistOnly() {
        // given
        Long locationId = 1L;

        Long previousContentId = 2L;
        Long previousArtistId = 3L;

        Long updatedContentId = 2L;
        Long updatedArtistId = 4L;

        TargetId previousTarget = new TargetId(locationId, previousContentId, previousArtistId);

        TargetId updatedTarget = new TargetId(locationId, updatedContentId, updatedArtistId);

        // when
        service.update(
                locationId,
                previousContentId,
                Set.of(previousArtistId),
                updatedContentId,
                Set.of(updatedArtistId));

        // then
        verifyNoInteractions(locationVisitRankingRepository);
        verifyNoInteractions(contentLocationVisitRankingRepository);

        verify(artistLocationVisitRankingRepository)
                .decreaseVerificationCount(locationId, previousArtistId);

        verify(artistLocationVisitRankingRepository)
                .increaseVerificationCount(locationId, updatedArtistId);

        verify(contentArtistVisitRankingRepository)
                .decreaseVerificationCount(previousContentId, previousArtistId);

        verify(contentArtistVisitRankingRepository)
                .increaseVerificationCount(updatedContentId, updatedArtistId);

        verify(contentArtistLocationVisitRankingRepository)
                .decreaseVerificationCount(previousTarget);

        verify(contentArtistLocationVisitRankingRepository)
                .increaseVerificationCount(updatedTarget);
    }

    @Test
    @DisplayName("콘텐츠와 아티스트가 모두 변경되면 관련 조합 랭킹을 모두 변경한다")
    void updateContentAndArtist() {
        // given
        Long locationId = 1L;

        Long previousContentId = 2L;
        Long previousArtistId = 3L;

        Long updatedContentId = 4L;
        Long updatedArtistId = 5L;

        TargetId previousTarget = new TargetId(locationId, previousContentId, previousArtistId);

        TargetId updatedTarget = new TargetId(locationId, updatedContentId, updatedArtistId);

        // when
        service.update(
                locationId,
                previousContentId,
                Set.of(previousArtistId),
                updatedContentId,
                Set.of(updatedArtistId));

        // then
        // 방문 자체는 그대로이므로 관광지 전체 랭킹은 변경하지 않음
        verifyNoInteractions(locationVisitRankingRepository);

        verify(contentLocationVisitRankingRepository)
                .decreaseVerificationCount(locationId, previousContentId);

        verify(contentLocationVisitRankingRepository)
                .increaseVerificationCount(locationId, updatedContentId);

        verify(artistLocationVisitRankingRepository)
                .decreaseVerificationCount(locationId, previousArtistId);

        verify(artistLocationVisitRankingRepository)
                .increaseVerificationCount(locationId, updatedArtistId);

        verify(contentArtistVisitRankingRepository)
                .decreaseVerificationCount(previousContentId, previousArtistId);

        verify(contentArtistVisitRankingRepository)
                .increaseVerificationCount(updatedContentId, updatedArtistId);

        verify(contentArtistLocationVisitRankingRepository)
                .decreaseVerificationCount(previousTarget);

        verify(contentArtistLocationVisitRankingRepository)
                .increaseVerificationCount(updatedTarget);
    }

    @Test
    @DisplayName("콘텐츠와 아티스트가 변경되지 않으면 랭킹을 변경하지 않는다")
    void updateWithoutChanges() {
        // given
        Long locationId = 1L;
        Long contentId = 2L;
        Long artistId = 3L;

        // when
        service.update(locationId, contentId, Set.of(artistId), contentId, Set.of(artistId));

        // then
        verifyNoInteractions(
                locationVisitRankingRepository,
                artistLocationVisitRankingRepository,
                contentLocationVisitRankingRepository,
                contentArtistVisitRankingRepository,
                contentArtistLocationVisitRankingRepository);
    }
}
