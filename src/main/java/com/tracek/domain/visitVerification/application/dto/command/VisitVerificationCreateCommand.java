package com.tracek.domain.visitVerification.application.dto.command;

import lombok.Builder;

@Builder
public record VisitVerificationCreateCommand(
        Long userId,
        Long locationId,
        Long contentId,
        Long artistId,
        Double latitude,
        Double longitude) {}
