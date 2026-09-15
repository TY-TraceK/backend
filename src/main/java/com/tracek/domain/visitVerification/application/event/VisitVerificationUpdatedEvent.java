package com.tracek.domain.visitVerification.application.event;

import com.tracek.domain.visitVerification.domain.model.VisitVerificationTarget;

public record VisitVerificationUpdatedEvent(
        Long locationId,
        Long previousContentId,
        Long previousArtistId,
        Long updatedContentId,
        Long updatedArtistId) {

    public static VisitVerificationUpdatedEvent of(
            Long locationId,
            VisitVerificationTarget previousTarget,
            VisitVerificationTarget updatedTarget) {
        return new VisitVerificationUpdatedEvent(
                locationId,
                previousTarget.getContentId(),
                previousTarget.getArtistId(),
                updatedTarget.getContentId(),
                updatedTarget.getArtistId());
    }
}
