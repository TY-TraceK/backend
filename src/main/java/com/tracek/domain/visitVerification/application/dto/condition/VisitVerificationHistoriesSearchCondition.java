package com.tracek.domain.visitVerification.application.dto.condition;

import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VisitVerificationHistoriesSearchCondition(
        Long userId,
        Long artistId,
        Long contentId,
        Long locationId,
        VisitVerificationStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate) {}
