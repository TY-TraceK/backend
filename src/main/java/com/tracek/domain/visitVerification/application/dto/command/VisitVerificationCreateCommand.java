package com.tracek.domain.visitVerification.application.dto.command;

import java.util.List;
import lombok.Builder;

@Builder
public record VisitVerificationCreateCommand(
        Long userId,
        Long locationId,
        Long contentId,
        List<Long> artistIds,
        Double latitude,
        Double longitude) {}
