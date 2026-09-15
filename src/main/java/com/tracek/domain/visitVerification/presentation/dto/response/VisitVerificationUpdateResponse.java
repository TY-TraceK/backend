package com.tracek.domain.visitVerification.presentation.dto.response;

import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationUpdateResult;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VisitVerificationUpdateResponse(
        Long visitVerificationId, String visitVerificationStatus, LocalDateTime visitVerifiedAt) {

    public static VisitVerificationUpdateResponse from(VisitVerificationUpdateResult result) {
        return VisitVerificationUpdateResponse.builder()
                .visitVerificationId(result.visitVerificationId())
                .visitVerificationStatus(result.visitVerificationStatus())
                .visitVerifiedAt(result.visitVerifiedAt())
                .build();
    }
}
