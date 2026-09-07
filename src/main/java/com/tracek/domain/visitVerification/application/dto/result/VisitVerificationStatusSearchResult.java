package com.tracek.domain.visitVerification.application.dto.result;

import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Builder;

@Builder
public record VisitVerificationStatusSearchResult(
        Boolean isvisitVerificationd, Long visitVerificationId, LocalDate targetDate) {

    public static VisitVerificationStatusSearchResult from(
            VisitVerification visitVerification, LocalDate targetDate) {
        return VisitVerificationStatusSearchResult.builder()
                .isvisitVerificationd(Objects.nonNull(visitVerification))
                .visitVerificationId(visitVerification != null ? visitVerification.getId() : null)
                .targetDate(targetDate)
                .build();
    }
}
