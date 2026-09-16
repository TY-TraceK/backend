package com.tracek.domain.visitVerification.application.dto.command;

import java.util.List;
import lombok.Builder;

@Builder
public record VisitVerificationUpdateCommand(
        Long visitVerificationId, Long userId, Long contentId, List<Long> artistIds) {}
