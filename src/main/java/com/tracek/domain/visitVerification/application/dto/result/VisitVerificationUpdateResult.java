package com.tracek.domain.visitVerification.application.dto.result;

import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VisitVerificationUpdateResult(
        Long visitVerificationId, String visitVerificationStatus, LocalDateTime visitVerifiedAt) {

    public static VisitVerificationUpdateResult from(VisitVerification visitVerification) {
        return VisitVerificationUpdateResult.builder()
                .visitVerificationId(visitVerification.getId())
                .visitVerifiedAt(visitVerification.getVerifiedAt())
                .build();
    }
}
