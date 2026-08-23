package com.tracek.domain.location.application.service;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.global.exception.CustomException;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;

// 낙관적 락 충돌(ObjectOptimisticLockingFailureException) 시 재시도만 담당. 실제 트랜잭션
// 로직은 LocationLikeTransactionalWriter에 있음 - 같은 빈 안에서 this.메서드()로 호출하면
// @Transactional 프록시를 안 거쳐서 트랜잭션이 안 걸리므로(self-invocation 문제) 반드시
// 별도 빈으로 분리해서 호출해야 함.
//
// 재시도 대기는 "지수 백오프 + Full Jitter" (AWS 아키텍처 블로그의 표준 패턴) 사용:
// sleep = random(0, min(CAP_MS, BASE_MS * 2^retryCount))
// 고정 지터(예: 30~80ms 랜덤)는 경합 인원이 많을 때 재시도끼리 계속 비슷한 타이밍에 다시
// 부딪히기 쉬워서, 재시도할수록 대기 범위 자체를 넓혀 서서히 풀리게 만드는 게 목적.
@Service
@RequiredArgsConstructor
public class LocationLikeCommandService {

    private static final int MAX_RETRY_COUNT = 10;
    private static final long BASE_DELAY_MS = 20;
    private static final long CAP_DELAY_MS = 500;
    private static final String DUPLICATE_LIKE_CONSTRAINT_NAME = "uk_location_like_user_location";

    private final LocationLikeTransactionalWriter locationLikeTransactionalWriter;

    public void like(Long userId, Long locationId) {
        int retryCount = 0;
        while (true) {
            try {
                locationLikeTransactionalWriter.like(userId, locationId);
                return;
            } catch (DataIntegrityViolationException e) {
                if (!isDuplicateLikeConstraintViolation(e)) {
                    throw e; // 중복 좋아요가 아닌 다른 무결성 위반은 그대로 전파
                }
                // 동시에 같은 유저가 중복 요청해서 UNIQUE 제약에 걸린 경우 - 결과적으로
                // 좋아요는 이미 저장된 상태이므로 성공으로 간주하고 재시도하지 않음
                return;
            } catch (ObjectOptimisticLockingFailureException e) {
                // 동시 충돌 발생
                retryCount++;
                if (retryCount >= MAX_RETRY_COUNT) {
                    throw new CustomException(
                            LocationErrorCode.CONCURRENCY_ERROR); // 재시도 다 실패하면 예외 처리
                }
                sleepWithBackoffJitter(retryCount);
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
                sleepWithBackoffJitter(retryCount);
            }
        }
    }

    private boolean isDuplicateLikeConstraintViolation(DataIntegrityViolationException e) {
        return e.getCause() instanceof ConstraintViolationException cve
                && DUPLICATE_LIKE_CONSTRAINT_NAME.equals(cve.getConstraintName());
    }

    private void sleepWithBackoffJitter(int retryCount) {
        long exponentialDelay = BASE_DELAY_MS * (1L << Math.min(retryCount, 20));
        long upperBound = Math.min(CAP_DELAY_MS, exponentialDelay);
        long randomDelay = ThreadLocalRandom.current().nextLong(0, upperBound + 1);
        try {
            Thread.sleep(randomDelay);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            // 인터럽트(취소)를 재시도로 덮지 않고 호출자에게 그대로 전파
            throw new IllegalStateException("재시도 대기 중 인터럽트됨", ex);
        }
    }
}
