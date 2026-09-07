package com.tracek.domain.visitVerification.presentation.dto.response;

import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesIndividualResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesResult;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record VisitVerificationHistoriesResponse(
        Page<VisitVerificationHistoriesIndividualResponse> histories) {

    public static VisitVerificationHistoriesResponse from(VisitVerificationHistoriesResult result) {

        return new VisitVerificationHistoriesResponse(
                result.histories().map(VisitVerificationHistoriesIndividualResponse::from));
    }
}

@Builder
record VisitVerificationHistoriesIndividualResponse(
        Long locationId,
        Long contentId,
        Long artistId,
        LocalDateTime visitVerifiedTimeAt,
        LocalDate visitVerifiedDate,
        String visitVerificationStatus) {

    public static VisitVerificationHistoriesIndividualResponse from(
            VisitVerificationHistoriesIndividualResult result) {
        return VisitVerificationHistoriesIndividualResponse.builder()
                .artistId(result.artistId())
                .contentId(result.contentId())
                .locationId(result.locationId())
                .visitVerifiedDate(result.visitVerifiedDate())
                .visitVerifiedTimeAt(result.visitVerifiedTimeAt())
                .visitVerificationStatus(result.visitVerificationStatus())
                .build();
    }
}
