package com.tracek.domain.visitVerification.application.dto.result;

import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import lombok.Builder;

@Builder
public record VisitVerificationCancelResult(
        Long visitVerificationId, String visitVerificationStatus) {

    public static VisitVerificationCancelResult from(VisitVerification verification) {
        return VisitVerificationCancelResult.builder()
                .visitVerificationId(verification.getId())
                .visitVerificationStatus(verification.getStatus().toString())
                .build();
    }
}
