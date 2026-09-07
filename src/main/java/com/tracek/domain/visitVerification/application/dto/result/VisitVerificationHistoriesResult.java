package com.tracek.domain.visitVerification.application.dto.result;

import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record VisitVerificationHistoriesResult(
        Page<VisitVerificationHistoriesIndividualResult> histories) {

    public static VisitVerificationHistoriesResult from(
            Page<VisitVerification> visitVerificationPage) {
        return new VisitVerificationHistoriesResult(
                visitVerificationPage.map(VisitVerificationHistoriesIndividualResult::from));
    }
}
