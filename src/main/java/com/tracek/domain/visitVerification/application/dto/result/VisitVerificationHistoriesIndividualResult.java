package com.tracek.domain.visitVerification.application.dto.result;

import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record VisitVerificationHistoriesIndividualResult(
        Long locationId,
        Long contentId,
        Long artistId,
        LocalDateTime visitVerifiedTimeAt,
        LocalDate visitVerifiedDate,
        String visitVerificationStatus) {

    public static VisitVerificationHistoriesIndividualResult from(
            VisitVerification visitVerification) {
        return new VisitVerificationHistoriesIndividualResult(
                visitVerification.getLocationId(),
                visitVerification.getVerificationTarget().getContentId(),
                visitVerification.getVerificationTarget().getArtistId(),
                visitVerification.getVerifiedAt(),
                visitVerification.getValidVerifiedAt(),
                visitVerification.getStatus().name());
    }
}
