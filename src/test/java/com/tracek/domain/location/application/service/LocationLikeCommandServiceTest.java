package com.tracek.domain.location.application.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.global.exception.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

// 재시도 로직만 검증 - 실제 비즈니스 로직(락/정합성)은 LocationLikeTransactionalWriterTest 참고.
@ExtendWith(MockitoExtension.class)
class LocationLikeCommandServiceTest {

    @Mock private LocationLikeTransactionalWriter locationLikeTransactionalWriter;

    private LocationLikeCommandService locationLikeCommandService;

    @BeforeEach
    void setUp() {
        locationLikeCommandService =
                new LocationLikeCommandService(locationLikeTransactionalWriter);
    }

    @Test
    @DisplayName("충돌 없이 성공하면 writer를 한 번만 호출한다")
    void like_success_noRetry() {
        locationLikeCommandService.like(1L, 1L);

        verify(locationLikeTransactionalWriter, times(1)).like(1L, 1L);
    }

    @Test
    @DisplayName("낙관적 락 충돌이 몇 번 나도 재시도 중 성공하면 예외 없이 끝난다")
    void like_retriesThenSucceeds() {
        willThrow(new ObjectOptimisticLockingFailureException(Object.class, 1L))
                .willThrow(new ObjectOptimisticLockingFailureException(Object.class, 1L))
                .willDoNothing()
                .given(locationLikeTransactionalWriter)
                .like(1L, 1L);

        locationLikeCommandService.like(1L, 1L);

        verify(locationLikeTransactionalWriter, times(3)).like(1L, 1L);
    }

    @Test
    @DisplayName("재시도를 다 소진하면 CONCURRENCY_ERROR 예외를 던진다")
    void like_exhaustsRetries_throwsConcurrencyError() {
        willThrow(new ObjectOptimisticLockingFailureException(Object.class, 1L))
                .given(locationLikeTransactionalWriter)
                .like(1L, 1L);

        assertThatThrownBy(() -> locationLikeCommandService.like(1L, 1L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.CONCURRENCY_ERROR);

        // MAX_RETRY_COUNT(5)번 시도 후 포기
        verify(locationLikeTransactionalWriter, times(5)).like(1L, 1L);
    }

    @Test
    @DisplayName(
            "같은 유저의 동시 요청이 UNIQUE 제약에 걸려도(DataIntegrityViolationException) "
                    + "이미 저장된 것으로 보고 재시도 없이 성공 처리한다")
    void like_duplicateInsert_treatedAsSuccess() {
        willThrow(new DataIntegrityViolationException("duplicate"))
                .given(locationLikeTransactionalWriter)
                .like(1L, 1L);

        locationLikeCommandService.like(1L, 1L);

        // 재시도 없이 딱 1번만 호출되고 끝나야 함
        verify(locationLikeTransactionalWriter, times(1)).like(1L, 1L);
    }

    @Test
    @DisplayName("unlike도 충돌 없이 성공하면 writer를 한 번만 호출한다")
    void unlike_success_noRetry() {
        locationLikeCommandService.unlike(1L, 1L);

        verify(locationLikeTransactionalWriter, times(1)).unlike(1L, 1L);
    }

    @Test
    @DisplayName("unlike도 재시도를 다 소진하면 CONCURRENCY_ERROR 예외를 던진다")
    void unlike_exhaustsRetries_throwsConcurrencyError() {
        willThrow(new ObjectOptimisticLockingFailureException(Object.class, 1L))
                .given(locationLikeTransactionalWriter)
                .unlike(1L, 1L);

        assertThatThrownBy(() -> locationLikeCommandService.unlike(1L, 1L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.CONCURRENCY_ERROR);

        verify(locationLikeTransactionalWriter, times(5)).unlike(1L, 1L);
        verify(locationLikeTransactionalWriter, never())
                .like(ArgumentMatchers.any(), ArgumentMatchers.any());
    }
}
