package com.tracek.domain.location.application.service;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationLike;
import com.tracek.domain.location.domain.repository.LocationRepository;
import com.tracek.domain.user.application.service.UserQueryService;
import com.tracek.domain.user.domain.exception.UserErrorCode;
import com.tracek.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 낙관적 락 실제 트랜잭션 로직. LocationLikeCommandService의 재시도 루프에서 프록시를
// 거쳐 호출되어야 @Transactional이 정상 동작하므로 별도 빈으로 분리함(self-invocation 방지).
@Service
@RequiredArgsConstructor
public class LocationLikeTransactionalWriter {

    private final LocationRepository locationRepository;
    private final UserQueryService userQueryService;

    @Transactional
    public void like(Long userId, Long locationId) {
        // 락 획득 (트랜잭션의 첫 DB 작업이어야 함)
        Location location =
                locationRepository
                        .findByIdWithOptimisticLock(locationId)
                        .orElseThrow(
                                () -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));

        validateActiveUser(userId);

        if (locationRepository.existsByUserIdAndLocationId(userId, locationId)) {
            return;
        }

        // 좋아요 저장 + Like Count 증가
        //
        // 주의: 낙관적 락은 동시 요청을 막지 않으므로(비관적 락과 달리) 같은 유저가 동시에
        // 여러 번 요청하면 둘 다 existsBy를 통과한 뒤 저장을 시도할 수 있음 -> UNIQUE 제약
        // 위반(DataIntegrityViolationException)이 실제로 발생 가능. 여기서 잡아서 삼키면
        // Spring이 이미 트랜잭션을 rollback-only로 표시해놓은 상태라 커밋 시점에
        // UnexpectedRollbackException이 대신 터짐 - 그래서 여기서 잡지 않고 그대로
        // 전파시켜서 LocationLikeCommandService(재시도 래퍼)에서 "이미 처리됨"으로 처리함.
        LocationLike like = LocationLike.of(userId, locationId);
        locationRepository.saveLike(like);
        location.increaseLikeCount();
    }

    @Transactional
    public void unlike(Long userId, Long locationId) {
        // 락 획득 (트랜잭션의 첫 DB 작업이어야 함 - like()와 동일한 이유)
        Location location =
                locationRepository
                        .findByIdWithOptimisticLock(locationId)
                        .orElseThrow(
                                () -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));

        validateActiveUser(userId);

        boolean exists = locationRepository.existsByUserIdAndLocationId(userId, locationId);
        if (!exists) {
            return;
        }

        locationRepository.deleteByUserIdAndLocationId(userId, locationId);
        location.decreaseLikeCount();
    }

    private void validateActiveUser(Long userId) {
        if (!userQueryService.isActiveUser(userId)) {
            throw new CustomException(UserErrorCode.USER_NOT_ACTIVATED);
        }
    }
}
