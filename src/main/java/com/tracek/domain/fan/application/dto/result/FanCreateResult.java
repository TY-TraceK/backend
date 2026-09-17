package com.tracek.domain.fan.application.dto.result;

import lombok.Builder;

@Builder
public record FanCreateResult(Long id, Long targetId) {

    public static FanCreateResult of(Long id, Long targetId) {
        return FanCreateResult.builder().id(id).targetId(targetId).build();
    }
}
