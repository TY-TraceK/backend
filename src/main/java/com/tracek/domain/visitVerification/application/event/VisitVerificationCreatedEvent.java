package com.tracek.domain.visitVerification.application.event;

import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VisitVerificationCreatedEvent(
        Long visitVerificationId,
        Long visitVerificationOwner,
        LocalDateTime visitVerificationdAt,
        Long locationId,
        Long artistId,
        Long contentId) {

    public static VisitVerificationCreatedEvent from(VisitVerification visitVerification) {
        return VisitVerificationCreatedEvent.builder()
                .visitVerificationId(visitVerification.getId())
                .visitVerificationOwner(visitVerification.getOwner())
                .visitVerificationdAt(visitVerification.getVerifiedAt())
                .locationId(visitVerification.getVerificationTarget().getLocationId())
                .artistId(visitVerification.getVerificationTarget().getArtistId())
                .contentId(visitVerification.getVerificationTarget().getContentId())
                .build();
    }
}
