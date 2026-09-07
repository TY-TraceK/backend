package com.tracek.domain.visitVerification.application.dto.condition;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record VisitVerificationStatusSearchCondition(
        Long userId, Long locationId, LocalDate date) {}
