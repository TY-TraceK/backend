package com.tracek.domain.visitVerification.domain.model;

import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VisitVerificationHistoryCriteria(
        Long userId,
        Long artistId,
        Long contentId,
        Long locationId,
        String city,
        VisitVerificationStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate,
        LocalDate cursorDate,
        Integer size) {}
