package com.tracek.domain.fan.application.dto.result;

import lombok.Builder;

@Builder
public record FanTargetIndividualResult(
        Long id,
        Long name,
        String description,
        String pictureUrl,
        Long fanCount,
        Long totalVerificationCount) {}
