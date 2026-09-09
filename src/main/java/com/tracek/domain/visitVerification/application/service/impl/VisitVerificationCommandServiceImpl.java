package com.tracek.domain.visitVerification.application.service.impl;

import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCancelCommand;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCreateCommand;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCancelResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCreateResult;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCanceledEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCreatedEvent;
import com.tracek.domain.visitVerification.application.service.VisitVerificationCommandService;
import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.exception.VisitVerificationErrorCode;
import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationTarget;
import com.tracek.domain.visitVerification.domain.repository.VisitVerificationRepository;
import com.tracek.global.exception.CustomException;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class VisitVerificationCommandServiceImpl implements VisitVerificationCommandService {

    private final VisitVerificationRepository visitVerificationRepository;
    private final LocationQueryService locationQueryService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final EpisodeQueryService episodeQueryService;

    private boolean hasAlreadyVerifiedLocation(Long userId, Long locationId) {
        return visitVerificationRepository
                .findUserLocationVerifiedByDate(userId, locationId, LocalDate.now())
                .isPresent();
    }

    private boolean isRelatedVerifiedTarget(Long locationId, Long contentId, Long artistId) {
        if (artistId == null) {
            return episodeQueryService.isRelatedContent(locationId, contentId);
        } else {
            return episodeQueryService.isRelatedContentAndArtist(locationId, contentId, artistId);
        }
    }

    private boolean isVisitZoneWithIn(Double latitude, Double longitude, Long locationId) {
        return !locationQueryService.isWithinDistance(latitude, longitude, 100, locationId);
    }

    @Transactional
    @Override
    public VisitVerificationCreateResult createVisitVerification(
            VisitVerificationCreateCommand command) {
        // 해당 관광지에 이미 방문 인증 했는 지 확인
        if (hasAlreadyVerifiedLocation(command.userId(), command.locationId())) {
            throw new CustomException(VisitVerificationErrorCode.ALREADY_VERIFIED);
        }
        // 연관되어 있는 지 확인
        if (!isRelatedVerifiedTarget(
                command.locationId(), command.contentId(), command.artistId())) {
            throw new CustomException(VisitVerificationErrorCode.VISIT_VERIFICATION_NOT_FOUND);
        }
        // 방문 가능한 위치인지 확인
        if (!isVisitZoneWithIn(command.latitude(), command.longitude(), command.locationId())) {
            throw new CustomException(VisitVerificationErrorCode.VISIT_ZONE_MISMATCH);
        }

        // 방문 인증하기
        VisitVerification visitVerification =
                VisitVerification.createvisitVerification(
                        command.userId(),
                        command.locationId(),
                        VisitVerificationTarget.of(command.artistId(), command.contentId()));

        VisitVerification savedvisitVerification =
                visitVerificationRepository.save(visitVerification);

        // 이벤트 발행
        applicationEventPublisher.publishEvent(
                VisitVerificationCreatedEvent.from(savedvisitVerification));
        return VisitVerificationCreateResult.from(savedvisitVerification);
    }

    @Override
    @Transactional
    public VisitVerificationCancelResult cancelVisitVerification(
            VisitVerificationCancelCommand command) {
        // 방문 인증 찾기
        VisitVerification visitVerification =
                visitVerificationRepository
                        .findById(command.visitVerificationId())
                        .orElseThrow(
                                () ->
                                        new CustomException(
                                                VisitVerificationErrorCode
                                                        .VISIT_VERIFICATION_NOT_FOUND));
        if (!Objects.equals(visitVerification.getOwner(), command.userId())) {
            throw new CustomException(VisitVerificationErrorCode.ACCESS_DINED);
        }
        // 이미 취소된 경우에는 별도 예외 처리를 하진 않음
        if (visitVerification.getStatus() == VisitVerificationStatus.VALID) {
            // 24시간 이내에만 삭제 가능
            if (Duration.between(visitVerification.getVerifiedAt(), LocalDateTime.now()).toHours()
                    >= 24) {
                throw new CustomException(VisitVerificationErrorCode.CANNOT_BE_CANCELLED);
            }
            visitVerification.invalid();
            // 이벤트 발행
            applicationEventPublisher.publishEvent(
                    VisitVerificationCanceledEvent.from(visitVerification));
        }
        return VisitVerificationCancelResult.from(visitVerification);
    }
}
