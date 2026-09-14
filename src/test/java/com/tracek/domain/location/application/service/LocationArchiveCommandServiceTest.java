package com.tracek.domain.location.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

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
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LocationArchiveCommandServiceTest {

    @Mock private LocationRepository locationRepository;
    @Mock private UserQueryService userQueryService;

    private LocationArchiveCommandService locationArchiveCommandService;

    @BeforeEach
    void setUp() {
        locationArchiveCommandService =
                new LocationArchiveCommandService(locationRepository, userQueryService);
    }

    @Test
    @DisplayName("활성 유저가 아카이브를 누르면 아카이브가 저장되고 카운트가 증가한다")
    void archive_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(userQueryService.isActiveUser(1L)).willReturn(true);
        given(locationRepository.existsArchiveByUserIdAndLocationId(1L, 1L)).willReturn(false);
        given(locationRepository.findByIdForUpdate(1L)).willReturn(Optional.of(location));

        locationArchiveCommandService.archive(1L, 1L);

        verify(locationRepository).saveArchive(org.mockito.ArgumentMatchers.any());
        assertThat(location.getArchiveCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("비활성/존재하지 않는 유저가 아카이브를 누르면 USER_NOT_ACTIVATED 예외가 발생한다")
    void archive_userNotActive() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(locationRepository.findByIdForUpdate(1L)).willReturn(Optional.of(location));
        given(userQueryService.isActiveUser(1L)).willReturn(false);

        assertThatThrownBy(() -> locationArchiveCommandService.archive(1L, 1L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(UserErrorCode.USER_NOT_ACTIVATED);

        InOrder inOrder = inOrder(locationRepository, userQueryService);
        inOrder.verify(locationRepository).findByIdForUpdate(1L);
        inOrder.verify(userQueryService).isActiveUser(1L);
        verify(locationRepository, never())
                .existsArchiveByUserIdAndLocationId(
                        org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verify(locationRepository, never()).saveArchive(org.mockito.ArgumentMatchers.any());
    }

    @Test
    @DisplayName("이미 아카이브한 상태면 저장 없이 조용히 종료한다")
    void archive_alreadyArchived() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(userQueryService.isActiveUser(1L)).willReturn(true);
        given(locationRepository.findByIdForUpdate(1L)).willReturn(Optional.of(location));
        given(locationRepository.existsArchiveByUserIdAndLocationId(1L, 1L)).willReturn(true);

        locationArchiveCommandService.archive(1L, 1L);

        verify(locationRepository, never()).saveArchive(org.mockito.ArgumentMatchers.any());
        assertThat(location.getArchiveCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("활성 유저가 아카이브를 취소하면 삭제되고 카운트가 감소한다")
    void deleteArchive_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        ReflectionTestUtils.setField(location, "archiveCount", 1L);
        given(userQueryService.isActiveUser(1L)).willReturn(true);
        given(locationRepository.existsArchiveByUserIdAndLocationId(1L, 1L)).willReturn(true);
        given(locationRepository.findByIdForUpdate(1L)).willReturn(Optional.of(location));

        locationArchiveCommandService.deleteArchive(1L, 1L);

        verify(locationRepository).deleteArchive(1L, 1L);
        assertThat(location.getArchiveCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("archiveCount가 null인 상태에서 아카이브를 취소해도 0으로 정규화된다")
    void deleteArchive_nullArchiveCount() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        ReflectionTestUtils.setField(location, "archiveCount", null);
        given(userQueryService.isActiveUser(1L)).willReturn(true);
        given(locationRepository.existsArchiveByUserIdAndLocationId(1L, 1L)).willReturn(true);
        given(locationRepository.findByIdForUpdate(1L)).willReturn(Optional.of(location));

        locationArchiveCommandService.deleteArchive(1L, 1L);

        assertThat(location.getArchiveCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("비활성/존재하지 않는 유저가 아카이브 취소를 시도하면 USER_NOT_ACTIVATED 예외가 발생한다")
    void deleteArchive_userNotActive() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(locationRepository.findByIdForUpdate(1L)).willReturn(Optional.of(location));
        given(userQueryService.isActiveUser(1L)).willReturn(false);

        assertThatThrownBy(() -> locationArchiveCommandService.deleteArchive(1L, 1L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(UserErrorCode.USER_NOT_ACTIVATED);

        InOrder inOrder = inOrder(locationRepository, userQueryService);
        inOrder.verify(locationRepository).findByIdForUpdate(1L);
        inOrder.verify(userQueryService).isActiveUser(1L);
        verify(locationRepository, never())
                .existsArchiveByUserIdAndLocationId(
                        org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verify(locationRepository, never())
                .deleteArchive(
                        org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
    }
}
