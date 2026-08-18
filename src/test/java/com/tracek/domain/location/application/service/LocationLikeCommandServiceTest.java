package com.tracek.domain.location.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.domain.location.domain.repository.LocationRepository;
import com.tracek.domain.user.application.service.UserQueryService;
import com.tracek.domain.user.domain.exception.UserErrorCode;
import com.tracek.global.exception.CustomException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocationLikeCommandServiceTest {

    @Mock private LocationRepository locationRepository;
    @Mock private UserQueryService userQueryService;

    private LocationLikeCommandService locationLikeCommandService;

    @BeforeEach
    void setUp() {
        locationLikeCommandService =
                new LocationLikeCommandService(locationRepository, userQueryService);
    }

    @Test
    @DisplayName("활성 유저가 좋아요를 누르면 좋아요가 저장되고 카운트가 증가한다")
    void like_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(userQueryService.isActiveUser(1L)).willReturn(true);
        given(locationRepository.existsByUserIdAndLocationId(1L, 1L)).willReturn(false);
        given(locationRepository.findByIdForUpdate(1L)).willReturn(Optional.of(location));

        locationLikeCommandService.like(1L, 1L);

        verify(locationRepository).saveLike(org.mockito.ArgumentMatchers.any());
        assertThat(location.getLikeCount()).isEqualTo(101L);
    }

    @Test
    @DisplayName("비활성/존재하지 않는 유저가 좋아요를 누르면 USER_NOT_ACTIVATED 예외가 발생한다")
    void like_userNotActive() {
        given(userQueryService.isActiveUser(1L)).willReturn(false);

        assertThatThrownBy(() -> locationLikeCommandService.like(1L, 1L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(UserErrorCode.USER_NOT_ACTIVATED);

        verifyNoInteractions(locationRepository);
    }

    @Test
    @DisplayName("이미 좋아요를 누른 상태면 저장 없이 조용히 종료한다")
    void like_alreadyLiked() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(userQueryService.isActiveUser(1L)).willReturn(true);
        given(locationRepository.findByIdForUpdate(1L)).willReturn(Optional.of(location));
        given(locationRepository.existsByUserIdAndLocationId(1L, 1L)).willReturn(true);

        locationLikeCommandService.like(1L, 1L);

        verify(locationRepository, never()).saveLike(org.mockito.ArgumentMatchers.any());
        assertThat(location.getLikeCount()).isEqualTo(100L);
    }

    @Test
    @DisplayName("활성 유저가 좋아요를 취소하면 삭제되고 카운트가 감소한다")
    void unlike_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(userQueryService.isActiveUser(1L)).willReturn(true);
        given(locationRepository.existsByUserIdAndLocationId(1L, 1L)).willReturn(true);
        given(locationRepository.findByIdForUpdate(1L)).willReturn(Optional.of(location));

        locationLikeCommandService.unlike(1L, 1L);

        verify(locationRepository).deleteByUserIdAndLocationId(1L, 1L);
        assertThat(location.getLikeCount()).isEqualTo(99L);
    }

    @Test
    @DisplayName("likeCount가 null인 상태에서 좋아요를 취소해도 0으로 정규화된다")
    void unlike_nullLikeCount() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", null);
        given(userQueryService.isActiveUser(1L)).willReturn(true);
        given(locationRepository.existsByUserIdAndLocationId(1L, 1L)).willReturn(true);
        given(locationRepository.findByIdForUpdate(1L)).willReturn(Optional.of(location));

        locationLikeCommandService.unlike(1L, 1L);

        assertThat(location.getLikeCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("비활성/존재하지 않는 유저가 좋아요 취소를 시도하면 USER_NOT_ACTIVATED 예외가 발생한다")
    void unlike_userNotActive() {
        given(userQueryService.isActiveUser(1L)).willReturn(false);

        assertThatThrownBy(() -> locationLikeCommandService.unlike(1L, 1L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(UserErrorCode.USER_NOT_ACTIVATED);

        verifyNoInteractions(locationRepository);
    }
}
