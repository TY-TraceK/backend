package com.tracek.domain.visitVerification.presentation.dto.response;

import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCreateResult;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VisitVerificationCreateResponse(
        Long visitVerificationId,
        String visitVerificationStatus,
        LocalDateTime visitVerificationdAt) {

    public static VisitVerificationCreateResponse from(VisitVerificationCreateResult result) {
        return VisitVerificationCreateResponse.builder()
                .visitVerificationId(result.visitVerificationId())
                .visitVerificationdAt(result.visitVerificationdAt())
                .visitVerificationStatus(result.visitVerificationStatus())
                .build();
    }
}
