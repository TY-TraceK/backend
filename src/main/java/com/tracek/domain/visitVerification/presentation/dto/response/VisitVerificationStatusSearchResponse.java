package com.tracek.domain.visitVerification.presentation.dto.response;

import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationStatusSearchResult;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record VisitVerificationStatusSearchResponse(
        Boolean isVisitVerified, Long visitVerificationId, LocalDate targetDate) {

    public static VisitVerificationStatusSearchResponse from(
            VisitVerificationStatusSearchResult result) {
        return VisitVerificationStatusSearchResponse.builder()
                .isVisitVerified(result.isVisitVerified())
                .visitVerificationId(result.visitVerificationId())
                .targetDate(result.targetDate())
                .build();
    }
}
