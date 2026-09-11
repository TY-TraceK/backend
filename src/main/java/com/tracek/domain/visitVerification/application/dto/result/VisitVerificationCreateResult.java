package com.tracek.domain.visitVerification.application.dto.result;

import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VisitVerificationCreateResult(
        Long visitVerificationId, String visitVerificationStatus, LocalDateTime visitVerifiedAt) {

    public static VisitVerificationCreateResult from(VisitVerification visitVerification) {
        return VisitVerificationCreateResult.builder()
                .visitVerificationId(visitVerification.getId())
                .visitVerifiedAt(visitVerification.getVerifiedAt())
                .build();
    }
}
