package com.tracek.domain.location.application.service;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.global.exception.CustomException;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

// 낙관적 락 충돌(ObjectOptimisticLockingFailureException) 시 재시도만 담당. 실제 트랜잭션
// 로직은 LocationLikeTransactionalWriter에 있음 - 같은 빈 안에서 this.메서드()로 호출하면
// @Transactional 프록시를 안 거쳐서 트랜잭션이 안 걸리므로(self-invocation 문제) 반드시
// 별도 빈으로 분리해서 호출해야 함.
@Service
@RequiredArgsConstructor
public class LocationLikeCommandService {

    private static final int MAX_RETRY_COUNT = 5;
    private static final long JITTER_MIN_MS = 30;
    private static final long JITTER_MAX_MS_EXCLUSIVE = 81;

    private final LocationLikeTransactionalWriter locationLikeTransactionalWriter;

    public void like(Long userId, Long locationId) {
        int retryCount = 0;
        while (true) {
            try {
                locationLikeTransactionalWriter.like(userId, locationId);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                // 동시 충돌 발생
                retryCount++;
                if (retryCount >= MAX_RETRY_COUNT) {
                    throw new CustomException(
                            LocationErrorCode.CONCURRENCY_ERROR); // 재시도 다 실패하면 예외 처리
                }
                sleepWithJitter();
            }
        }
    }

    public void unlike(Long userId, Long locationId) {
        int retryCount = 0;
        while (true) {
            try {
                locationLikeTransactionalWriter.unlike(userId, locationId);
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                // 동시 충돌 발생
                retryCount++;
                if (retryCount >= MAX_RETRY_COUNT) {
                    throw new CustomException(
                            LocationErrorCode.CONCURRENCY_ERROR); // 재시도 다 실패하면 예외 처리
                }
                sleepWithJitter();
            }
        }
    }

    private void sleepWithJitter() {
        // 랜덤 지터 (30ms ~ 80ms 사이의 랜덤 대기) - 동시에 깨어나서 다시 부딪히는 것 방지
        long randomDelay =
                ThreadLocalRandom.current().nextLong(JITTER_MIN_MS, JITTER_MAX_MS_EXCLUSIVE);
        try {
            Thread.sleep(randomDelay);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
    }
}
