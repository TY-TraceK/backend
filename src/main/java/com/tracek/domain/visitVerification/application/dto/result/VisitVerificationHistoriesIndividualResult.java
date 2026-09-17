package com.tracek.domain.visitVerification.application.dto.result;

import com.tracek.domain.visitVerification.domain.model.VisitVerificationView;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationView.ArtistView;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record VisitVerificationHistoriesIndividualResult(
        Long visitVerificationId,
        Long locationId,
        String locationName,
        String locationAddress,
        String locationImageUrl,
        String city,
        Long contentId,
        String contentTitle,
        List<ArtistViewResult> artists,
        LocalDate visitVerifiedDate,
        LocalDateTime visitVerifiedTimeAt,
        String visitVerificationStatus,
        boolean liked,
        boolean archived) {

    public static VisitVerificationHistoriesIndividualResult from(VisitVerificationView view) {

        return VisitVerificationHistoriesIndividualResult.builder()
                .visitVerificationId(view.visitVerificationId())
                .locationId(view.locationId())
                .locationName(view.locationName())
                .locationAddress(view.locationAddress())
                .locationImageUrl(view.locationImageUrl())
                .city(view.city())
                .visitVerifiedDate(view.visitVerifiedDate())
                .contentId(view.contentId())
                .liked(view.liked())
                .archived(view.archived())
                .contentTitle(view.contentTitle())
                .artists(view.artists().stream().map(ArtistViewResult::from).toList())
                .visitVerifiedTimeAt(view.visitVerifiedTimeAt())
                .visitVerificationStatus(view.visitVerificationStatus().name())
                .build();
    }

    @Builder
    public record ArtistViewResult(Long artistId, String artistName) {

        public static ArtistViewResult from(ArtistView artistView) {
            return ArtistViewResult.builder()
                    .artistId(artistView.artistId())
                    .artistName(artistView.artistName())
                    .build();
        }
    }
}
