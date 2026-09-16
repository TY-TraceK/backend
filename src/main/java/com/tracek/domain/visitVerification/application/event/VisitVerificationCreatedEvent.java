package com.tracek.domain.visitVerification.application.event;

import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record VisitVerificationCreatedEvent(
        Long visitVerificationId,
        Long visitVerificationOwner,
        LocalDateTime visitVerifiedAt,
        Long locationId,
        Long artistId,
        Set<Long> artistIds,
        Long contentId) {

    public static VisitVerificationCreatedEvent from(VisitVerification visitVerification) {
        return VisitVerificationCreatedEvent.builder()
                .visitVerificationId(visitVerification.getId())
                .visitVerificationOwner(visitVerification.getOwner())
                .visitVerifiedAt(visitVerification.getVerifiedAt())
                .locationId(visitVerification.getLocationId())
                .artistIds(visitVerification.getVerificationTarget().getArtistIds())
                .contentId(visitVerification.getVerificationTarget().getContentId())
                .build();
    }
}
