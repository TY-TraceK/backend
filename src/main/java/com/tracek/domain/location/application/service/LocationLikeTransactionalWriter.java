package com.tracek.domain.location.application.service;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationLike;
import com.tracek.domain.location.domain.repository.LocationRepository;
import com.tracek.domain.user.application.service.UserQueryService;
import com.tracek.domain.user.domain.exception.UserErrorCode;
import com.tracek.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
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
        try {
            LocationLike like = LocationLike.of(userId, locationId);
            locationRepository.saveLike(like);
            location.increaseLikeCount();
        } catch (DataIntegrityViolationException e) {
            // 방어적으로 남겨둠: 정상 흐름에선 낙관적 락(버전 충돌)이 먼저 걸려서 여기 도달하지 않음.
        }
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
