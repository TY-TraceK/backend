package com.tracek.domain.visitVerification.presentation.dto.request;

import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VisitVerificationCreateRequest(
        @NotNull Long locationId,
        @NotNull Long locationContentArtistId,
        @NotBlank String visitVerificationTargetNameSnapShot) {

    public VisitVerificationCreateCommand toCommand(Long userId) {
        return VisitVerificationCreateCommand.builder()
                .userId(userId)
                .locationContentArtistId(locationContentArtistId)
                .locationId(locationId)
                .visitVerificationTargetNameSnapShot(visitVerificationTargetNameSnapShot)
                .build();
    }
}
