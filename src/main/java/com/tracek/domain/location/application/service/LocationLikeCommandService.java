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

        validateActiveUser(userId);

        // 락 획득
        Location location = locationRepository
                .findByIdForUpdate(locationId)
                .orElseThrow(
                        () -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));


        // 이미 좋아요를 눌렀는지 확인 (위에서 location row 락을 먼저 잡기 때문에 동시 요청도 순차
        // 처리됨 — 완전한 멱등성 보장)
        if (locationRepository.existsByUserIdAndLocationId(userId, locationId)) {
            return;
        }

        // 좋아요 저장 + Like Count 증가
        try {
            LocationLike like = LocationLike.of(userId, locationId);
            locationRepository.saveLike(like);
            location.increaseLikeCount();
        } catch (DataIntegrityViolationException e) {
            // 방어적으로 남겨둠: findByIdForUpdate로 락을 먼저 잡기 때문에 정상 흐름에선
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

        validateActiveUser(userId);

        // 락 획득
        Location location =
                locationRepository.findByIdForUpdate(locationId)
                        .orElseThrow(
                                () -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));

        boolean exists = locationRepository.existsByUserIdAndLocationId(userId, locationId);
        if (!exists) {
            return;
        }

        locationRepository.deleteByUserIdAndLocationId(userId, locationId);
        location.decreaseLikeCount();
    }
}
