package com.tracek.domain.visitVerification.presentation.dto.response;

import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesIndividualResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesIndividualResult.ArtistViewResult;
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
        Long visitVerificationId,
        Long locationId,
        String locationName,
        String locationAddress,
        String locationImageUrl,
        String city,
        Long contentId,
        String contentTitle,
        List<ArtistViewResponse> artists,
        LocalDateTime visitVerifiedTimeAt,
        String visitVerificationStatus,
        boolean liked,
        boolean archived) {

    public static VisitVerificationHistoriesIndividualResponse from(
            VisitVerificationHistoriesIndividualResult result) {

        return VisitVerificationHistoriesIndividualResponse.builder()
                .visitVerificationId(result.visitVerificationId())
                .locationId(result.locationId())
                .locationName(result.locationName())
                .locationAddress(result.locationAddress())
                .locationImageUrl(result.locationImageUrl())
                .city(result.city())
                .liked(result.liked())
                .archived(result.archived())
                .contentId(result.contentId())
                .contentTitle(result.contentTitle())
                .artists(result.artists().stream().map(ArtistViewResponse::from).toList())
                .visitVerifiedTimeAt(result.visitVerifiedTimeAt())
                .visitVerificationStatus(result.visitVerificationStatus())
                .build();
    }

    @Builder
    public record ArtistViewResponse(Long artistId, String artistName) {

        public static ArtistViewResponse from(ArtistViewResult viewResult) {
            return ArtistViewResponse.builder()
                    .artistId(viewResult.artistId())
                    .artistName(viewResult.artistName())
                    .build();
        }
    }
}
