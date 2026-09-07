package com.tracek.domain.visitVerification.domain.model;

import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VisitVerificationHistoryCriteria(
        Long userId,
        Long artistId,
        Long contentId,
        Long locationId,
        VisitVerificationStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate) {}
