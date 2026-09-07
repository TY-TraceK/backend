package com.tracek.domain.visitVerification.application.service.impl;

import com.tracek.domain.location.application.dto.LocationContentArtistResult;
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
import java.time.LocalDate;
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

    @Transactional
    @Override
    public VisitVerificationCreateResult createvisitVerification(
            VisitVerificationCreateCommand command) {
        // 해당 관광지에 이미 투표 했는 지 확인
        if (visitVerificationRepository.hasAlreadyVerifiedLocation(
                command.userId(), command.locationId())) {
            throw new CustomException(VisitVerificationErrorCode.ALREADY_VERIFIED);
        }
        // 해당 관광지-아티스트-콘텐츠 테이블 결과 가져오기
        LocationContentArtistResult locationContentArtistResult =
                locationQueryService.getMappingById(command.locationContentArtistId());
        // 투표 하기
        VisitVerification visitVerification =
                VisitVerification.createvisitVerification(
                        command.userId(),
                        VisitVerificationTarget.of(
                                locationContentArtistResult.getLocationId(),
                                command.locationContentArtistId(),
                                locationContentArtistResult.getArtistId(),
                                locationContentArtistResult.getContentId(),
                                command.visitVerificationTargetNameSnapShot()));
        VisitVerification savedvisitVerification =
                visitVerificationRepository.save(visitVerification);

        // 이벤트 발행
        applicationEventPublisher.publishEvent(
                VisitVerificationCreatedEvent.from(savedvisitVerification));
        return VisitVerificationCreateResult.from(savedvisitVerification);
    }

    @Override
    @Transactional
    public VisitVerificationCancelResult cancelvisitVerification(
            VisitVerificationCancelCommand command) {
        // 투표 찾기
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
            // 오늘 것만 취소 가능
            if (!Objects.equals(visitVerification.getValidVerifiedAt(), LocalDate.now())) {
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
