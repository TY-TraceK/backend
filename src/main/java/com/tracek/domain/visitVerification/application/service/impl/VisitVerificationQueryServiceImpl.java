package com.tracek.domain.visitVerification.application.service.impl;

import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationHistoriesSearchCondition;
import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationStatusSearchCondition;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationStatusSearchResult;
import com.tracek.domain.visitVerification.application.dto.result.VerificationLocationResult;
import com.tracek.domain.visitVerification.application.repository.VerificationLocationRepository;
import com.tracek.domain.location.domain.model.GeoLocation;
import com.tracek.domain.visitVerification.application.service.VisitVerificationQueryService;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationHistoryCriteria;
import com.tracek.domain.visitVerification.domain.repository.VisitVerificationRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class VisitVerificationQueryServiceImpl implements VisitVerificationQueryService {

    private final VisitVerificationRepository visitVerificationRepository;
    private final VerificationLocationRepository verificationLocationRepository;

    private static final double BUSAN_MIN_LATITUDE = 34.879;
    private static final double BUSAN_MAX_LATITUDE = 35.395;
    private static final double BUSAN_MIN_LONGITUDE = 128.738;
    private static final double BUSAN_MAX_LONGITUDE = 129.315;

    @Override
    public VisitVerificationStatusSearchResult getMyVisitVerificationStatus(
            VisitVerificationStatusSearchCondition condition) {
        return VisitVerificationStatusSearchResult.from(
                visitVerificationRepository
                        .findUserLocationVerifiedByDate(
                                condition.userId(), condition.locationId(), condition.date())
                        .orElse(null),
                condition.date());
    }

    @Override
    @Transactional(readOnly = true)
    public VisitVerificationHistoriesResult getMyHistories(
            VisitVerificationHistoriesSearchCondition condition) {
        return VisitVerificationHistoriesResult.of(
                visitVerificationRepository.findHistoriesByCriteria(
                        VisitVerificationHistoryCriteria.builder()
                                .userId(condition.userId())
                                .contentId(condition.contentId())
                                .artistId(condition.artistId())
                                .locationId(condition.locationId())
                                .startDate(condition.startDate())
                                .endDate(condition.endDate())
                                .status(condition.status())
                                .cursorDate(condition.cursorDate())
                                .city(condition.city())
                                .size(condition.size())
                                .build()),
                condition.size());
    }
    @Override
    @Transactional(readOnly = true)
    public VerificationLocationResult getVerificationLocationCandidates(
            double latitude, double longitude) {
        GeoLocation.validateRange(latitude, longitude);
        if (isInBusan(latitude, longitude)) {
            return VerificationLocationResult.inBusan(List.of());
        }
        return new VerificationLocationResult(
                false, verificationLocationRepository.findVerificationLocationCandidates());
    }

    private boolean isInBusan(double latitude, double longitude) {
        return latitude >= BUSAN_MIN_LATITUDE
                && latitude <= BUSAN_MAX_LATITUDE
                && longitude >= BUSAN_MIN_LONGITUDE
                && longitude <= BUSAN_MAX_LONGITUDE;
    }
}
