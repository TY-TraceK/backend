package com.tracek.domain.visitVerification.application.dto.command;

import lombok.Builder;

@Builder
public record VisitVerificationUpdateCommand(
        Long visitVerificationId, Long userId, Long contentId, Long artistId) {}
