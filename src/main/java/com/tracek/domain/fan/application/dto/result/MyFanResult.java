package com.tracek.domain.fan.application.dto.result;

import java.util.List;
import lombok.Builder;

@Builder
public record MyFanResult(
        List<FanTargetResult> artists, List<FanTargetResult> contents) {}
