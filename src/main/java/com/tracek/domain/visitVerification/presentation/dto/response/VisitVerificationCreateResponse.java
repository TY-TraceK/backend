package com.tracek.domain.visitVerification.presentation.dto.response;

import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCreateResult;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VisitVerificationCreateResponse(
        Long visitVerificationId, String visitVerificationStatus, LocalDateTime visitVerifiedAt) {

    public static VisitVerificationCreateResponse from(VisitVerificationCreateResult result) {
        return VisitVerificationCreateResponse.builder()
                .visitVerificationId(result.visitVerificationId())
                .visitVerifiedAt(result.visitVerifiedAt())
                .visitVerificationStatus(result.visitVerificationStatus())
                .build();
    }
}
