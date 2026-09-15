package com.tracek.domain.visitVerification.presentation.dto.request;

import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationUpdateCommand;
import jakarta.validation.constraints.NotNull;

public record VisitVerificationUpdateRequest(@NotNull Long contentId, @NotNull Long artistId) {

    public VisitVerificationUpdateCommand toCommand(Long visitVerificationId, Long userId) {
        return VisitVerificationUpdateCommand.builder()
                .visitVerificationId(visitVerificationId)
                .contentId(contentId)
                .userId(userId)
                .artistId(artistId)
                .build();
    }
}
