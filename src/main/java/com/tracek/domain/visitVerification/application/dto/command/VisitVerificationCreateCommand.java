package com.tracek.domain.visitVerification.application.dto.command;

import lombok.Builder;

@Builder
public record VisitVerificationCreateCommand(
        Long userId,
        Long locationId,
        Long locationContentArtistId,
        String visitVerificationTargetNameSnapShot) {}
