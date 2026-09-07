package com.tracek.domain.visitVerification.presentation.dto.request;

import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationHistoriesSearchCondition;
import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDate;

public record VisitVerificationHistoriesSearchRequest(
        Long artistId,
        Long contentId,
        Long locationId,
        VisitVerificationStatus status,
        LocalDate startDate,
        LocalDate endDate) {

    public VisitVerificationHistoriesSearchCondition toCondition(Long userId) {
        return VisitVerificationHistoriesSearchCondition.builder()
                .artistId(artistId)
                .contentId(contentId)
                .locationId(locationId)
                .status(status)
                .startDate(startDate != null ? startDate.atStartOfDay() : null)
                .endDate(endDate != null ? endDate.plusDays(1).atStartOfDay() : null)
                .userId(userId)
                .build();
    }

    @AssertTrue(message = "시작 날짜는 종료 날짜보다 늦을 수 없습니다.")
    private boolean isValidRange() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !startDate.isAfter(endDate);
    }
}
