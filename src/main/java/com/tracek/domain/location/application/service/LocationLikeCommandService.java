package com.tracek.domain.location.application.service;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationLike;
import com.tracek.domain.location.domain.repository.LocationRepository;
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

    public void like(Long userId, Long locationId) {

        // 이미 좋아요를 눌렀는지 확인 (순차 재요청 기준 멱등성 보장 — 동시 요청 시엔 데이터 정합성만 보장, 완전한 멱등성은 미보장)
        if (locationRepository.existsByUserIdAndLocationId(userId, locationId)) {
            return;
        }

        // 장소 존재 여부 확인
        Location location =
                locationRepository
                        .findById(locationId)
                        .orElseThrow(
                                () -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));

        // 좋아요 저장 (동시성 요청 시 DB UNIQUE 제약조건 위반 예외 발생 가능 지점)
        try {
            LocationLike like = LocationLike.of(userId, locationId);
            locationRepository.saveLike(like);
            // Like Count 증가 (Lost Update 발생 가능 지점)
            location.increaseLikeCount();
        } catch (DataIntegrityViolationException e) {
            // 동시에 들어온 요청으로 인해 UNIQUE 제약조건 위반 시 무시 (데이터는 중복 저장되지 않음)
            // 단, 이 경로를 탄 "진 쪽" 요청은 트랜잭션 커밋 시점에 예외가 재발생할 수 있어 완전한 요청 단위 멱등성은 아님
            // (부하 테스트 시 DB 예외/Rollback 및 TPS 저하의 주 원인이 됨 — Redisson 분산락 적용 전까지 알려진 한계)
        }
    }

    public void unlike(Long userId, Long locationId) {
        boolean exists = locationRepository.existsByUserIdAndLocationId(userId, locationId);
        if (!exists) {
            return;
        }

        // 장소 존재 여부 확인
        Location location =
                locationRepository
                        .findById(locationId)
                        .orElseThrow(
                                () -> new CustomException(LocationErrorCode.LOCATION_NOT_FOUND));

        locationRepository.deleteByUserIdAndLocationId(userId, locationId);
        location.decreaseLikeCount();
    }
}
