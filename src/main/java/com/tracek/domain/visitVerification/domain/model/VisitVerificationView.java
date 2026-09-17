package com.tracek.domain.visitVerification.domain.model;

import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;

@Builder
public record VisitVerificationView(
        Long visitVerificationId,
        Long locationId,
        String locationName,
        String locationAddress,
        String locationImageUrl,
        String city,
        Long contentId,
        String contentTitle,
        List<ArtistView> artists,
        LocalDate visitVerifiedDate,
        LocalDateTime visitVerifiedTimeAt,
        VisitVerificationStatus visitVerificationStatus,
        boolean liked,
        boolean archived) {

    @Builder
    public record ArtistView(Long artistId, String artistName) {}
}
