package com.tracek.domain.visitVerification.presentation.dto.request;

import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationStatusSearchCondition;
import java.time.LocalDate;

public record VisitVerificationStatusSearchRequest(LocalDate date) {

    public VisitVerificationStatusSearchCondition toCondition(Long locationId, Long userId) {
        return VisitVerificationStatusSearchCondition.builder()
                .userId(userId)
                .locationId(locationId)
                .date(date == null ? LocalDate.now() : date)
                .build();
    }
}
