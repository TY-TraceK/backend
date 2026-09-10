package com.tracek.domain.visitVerification.presentation.dto.response;

import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesIndividualResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesResult;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record VisitVerificationHistoriesResponse(
        List<VisitVerificationHistoriesGroupResponse> histories,
        boolean hasNext,
        LocalDate nextCursorDate) {

    public static VisitVerificationHistoriesResponse from(VisitVerificationHistoriesResult result) {
        List<VisitVerificationHistoriesGroupResponse> groupResponses =
                result.histories().entrySet().stream()
                        .map(
                                entry ->
                                        VisitVerificationHistoriesGroupResponse.of(
                                                entry.getKey(), entry.getValue()))
                        .toList();

        return VisitVerificationHistoriesResponse.builder()
                .histories(groupResponses)
                .hasNext(result.hasNext())
                .nextCursorDate(result.nextCursorDate())
                .build();
    }
}

@Builder
record VisitVerificationHistoriesGroupResponse(
        LocalDate date, List<VisitVerificationHistoriesIndividualResponse> items) {

    public static VisitVerificationHistoriesGroupResponse of(
            LocalDate date, List<VisitVerificationHistoriesIndividualResult> results) {
        return VisitVerificationHistoriesGroupResponse.builder()
                .date(date)
                .items(
                        results.stream()
                                .map(VisitVerificationHistoriesIndividualResponse::from)
                                .toList())
                .build();
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
