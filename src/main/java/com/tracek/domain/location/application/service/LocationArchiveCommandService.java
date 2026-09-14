package com.tracek.domain.location.application.service;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationArchive;
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
public class LocationArchiveCommandService {

    private final LocationRepository locationRepository;
    private final UserQueryService userQueryService;

    public void archive(Long userId, Long locationId) {

        // 락 획득 (트랜잭션의 첫 DB 작업이어야 함)
        Location location =
                locationRepository
                        .findByIdForUpdate(locationId)
                        .orElseThrow(
                                () -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));

        validateActiveUser(userId);

        if (locationRepository.existsArchiveByUserIdAndLocationId(userId, locationId)) {
            return;
        }

        try {
            LocationArchive archive = LocationArchive.of(userId, locationId);
            locationRepository.saveArchive(archive);
            location.increaseArchiveCount();
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

    public void deleteArchive(Long userId, Long locationId) {
        // 락 획득 (트랜잭션의 첫 DB 작업이어야 함)
        Location location =
                locationRepository
                        .findByIdForUpdate(locationId)
                        .orElseThrow(
                                () -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));

        validateActiveUser(userId);

        boolean exists = locationRepository.existsArchiveByUserIdAndLocationId(userId, locationId);
        if (!exists) {
            return;
        }

        locationRepository.deleteArchive(userId, locationId);
        location.decreaseArchiveCount();
    }
}
