package com.tracek.domain.visitVerification.application.dto.command;

import lombok.Builder;

@Builder
public record VisitVerificationCancelCommand(Long visitVerificationId, Long userId) {

    public static VisitVerificationCancelCommand of(Long visitVerificationId, Long userId) {
        return VisitVerificationCancelCommand.builder()
                .visitVerificationId(visitVerificationId)
                .userId(userId)
                .build();
    }
}
