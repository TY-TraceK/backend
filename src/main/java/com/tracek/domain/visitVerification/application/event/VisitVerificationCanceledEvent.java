package com.tracek.domain.visitVerification.application.event;

import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.Builder;

@Builder
public record VisitVerificationCanceledEvent(
        Long visitVerificationId,
        Long visitVerificationOwner,
        LocalDateTime verifiedAt,
        Long locationId,
        Set<Long> artistIds,
        Long contentId) {

    public static VisitVerificationCanceledEvent from(VisitVerification visitVerification) {
        return VisitVerificationCanceledEvent.builder()
                .visitVerificationId(visitVerification.getId())
                .visitVerificationOwner(visitVerification.getOwner())
                .verifiedAt(visitVerification.getVerifiedAt())
                .locationId(visitVerification.getLocationId())
                .artistIds(Set.copyOf(visitVerification.getVerificationTarget().getArtistIds()))
                .contentId(visitVerification.getVerificationTarget().getContentId())
                .build();
    }
}
