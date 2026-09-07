package com.tracek.domain.visitVerification.presentation.dto.response;

import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationStatusSearchResult;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record VisitVerificationStatusSearchResponse(
        Boolean isvisitVerificationd, Long visitVerificationId, LocalDate targetDate) {

    public static VisitVerificationStatusSearchResponse from(
            VisitVerificationStatusSearchResult result) {
        return VisitVerificationStatusSearchResponse.builder()
                .isvisitVerificationd(result.isvisitVerificationd())
                .visitVerificationId(result.visitVerificationId())
                .targetDate(result.targetDate())
                .build();
    }
}
