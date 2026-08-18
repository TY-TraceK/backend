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

@Service
@RequiredArgsConstructor
@Transactional
public class LocationLikeCommandService {

    private final LocationRepository locationRepository;
    private final UserQueryService userQueryService;

    public void like(Long userId, Long locationId) {

        // 락 획득 (트랜잭션의 첫 DB 작업이어야 함 - 아래 참고)
        Location location =
                locationRepository
                        .findByIdForUpdate(locationId)
                        .orElseThrow(
                                () -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));

        validateActiveUser(userId);

        // 이미 좋아요를 눌렀는지 확인 (위에서 location row 락을 먼저 잡기 때문에 동시 요청도 순차
        // 처리됨 — 완전한 멱등성 보장)
        //
        // 주의: findByIdForUpdate가 반드시 이 메서드의 "첫 DB 작업"이어야 함. MySQL
        // REPEATABLE READ에서는 일반 SELECT(락 없는 읽기)가 트랜잭션 내 첫 읽기를 하는 순간
        // 스냅샷이 고정되고, 이후의 다른 일반 SELECT(existsBy 등)도 그 스냅샷을 그대로 씀.
        // 락을 건 읽기(FOR UPDATE)는 스냅샷을 무시하고 항상 최신 커밋값을 읽지만, 그게 두
        // 번째 DB 작업이면 이미 첫 번째 일반 SELECT 시점에 스냅샷이 고정된 뒤라 소용없음.
        // (실제로 validateActiveUser를 먼저 호출했다가, 락 대기 중 다른 트랜잭션이 커밋한
        // 걸 existsBy가 못 보고 중복 INSERT를 시도해 UNIQUE 위반 500이 실측으로 재현됨 -
        // 부하테스트 30건 중 4건. LOAD_TEST_LOG.md 참고)
        if (locationRepository.existsByUserIdAndLocationId(userId, locationId)) {
            return;
        }

        // 좋아요 저장 + Like Count 증가
        try {
            LocationLike like = LocationLike.of(userId, locationId);
            locationRepository.saveLike(like);
            location.increaseLikeCount();
        } catch (DataIntegrityViolationException e) {
            // 방어적으로 남겨둠: findByIdForUpdate가 트랜잭션의 첫 DB 작업이면 정상 흐름에선
            // 여기 도달하지 않음(UNIQUE 위반도, Lost Update도 락으로 직렬화되어 방지됨).
            // 락 없이 우회하는 코드 경로가 생기지 않는 한 안전.
        }
    }

    private void validateActiveUser(Long userId) {
        if (!userQueryService.isActiveUser(userId)) {
            throw new CustomException(UserErrorCode.USER_NOT_ACTIVATED);
        }
    }

    public void unlike(Long userId, Long locationId) {

        // 락 획득 (트랜잭션의 첫 DB 작업이어야 함 - like()의 스냅샷 관련 주석 참고)
        Location location =
                locationRepository
                        .findByIdForUpdate(locationId)
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
}
