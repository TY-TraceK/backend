package com.tracek.domain.visitVerification.presentation.dto.request;

import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationUpdateCommand;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record VisitVerificationUpdateRequest(
        @NotNull Long contentId, @NotEmpty List<Long> artistIds) {

    public VisitVerificationUpdateCommand toCommand(Long visitVerificationId, Long userId) {
        return VisitVerificationUpdateCommand.builder()
                .visitVerificationId(visitVerificationId)
                .contentId(contentId)
                .userId(userId)
                .artistIds(artistIds)
                .build();
    }
}
