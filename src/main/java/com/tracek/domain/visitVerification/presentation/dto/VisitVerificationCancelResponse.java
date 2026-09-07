package com.tracek.domain.visitVerification.presentation.dto;

import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCancelResult;
import lombok.Builder;

@Builder
public record VisitVerificationCancelResponse(
        Long visitVerificationId, String visitVerificationStatus) {

    public static VisitVerificationCancelResponse from(VisitVerificationCancelResult result) {
        return VisitVerificationCancelResponse.builder()
                .visitVerificationId(result.visitVerificationId())
                .visitVerificationStatus(result.visitVerificationStatus())
                .build();
    }
}
