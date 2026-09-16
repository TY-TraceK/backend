package com.tracek.domain.visitVerification.application.dto.result;

import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record VisitVerificationHistoriesIndividualResult(
        Long locationId,
        Long contentId,
        Set<Long> artistIds,
        LocalDateTime visitVerifiedTimeAt,
        LocalDate visitVerifiedDate,
        String visitVerificationStatus) {

    public static VisitVerificationHistoriesIndividualResult from(
            VisitVerification visitVerification) {
        return new VisitVerificationHistoriesIndividualResult(
                visitVerification.getLocationId(),
                visitVerification.getVerificationTarget().getContentId(),
                Set.copyOf(visitVerification.getVerificationTarget().getArtistIds()),
                visitVerification.getVerifiedAt(),
                visitVerification.getValidVerifiedAt(),
                visitVerification.getStatus().name());
    }
}
