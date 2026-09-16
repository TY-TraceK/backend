package com.tracek.domain.visitVerification.application.event;

import com.tracek.domain.visitVerification.domain.model.VisitVerificationTarget;
import java.util.Set;

public record VisitVerificationUpdatedEvent(
        Long locationId,
        Long previousContentId,
        Set<Long> previousArtistIds,
        Long updatedContentId,
        Set<Long> updatedArtistIds) {

    public static VisitVerificationUpdatedEvent of(
            Long locationId,
            VisitVerificationTarget previousTarget,
            VisitVerificationTarget updatedTarget) {
        return new VisitVerificationUpdatedEvent(
                locationId,
                previousTarget.getContentId(),
                Set.copyOf(previousTarget.getArtistIds()),
                updatedTarget.getContentId(),
                Set.copyOf(updatedTarget.getArtistIds()));
    }
}
