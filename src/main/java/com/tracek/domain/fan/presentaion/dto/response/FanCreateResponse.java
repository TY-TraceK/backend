package com.tracek.domain.fan.presentaion.dto.response;

import com.tracek.domain.fan.application.dto.result.FanCreateResult;
import lombok.Builder;

@Builder
public record FanCreateResponse(Long id, Long targetId) {

    public static FanCreateResponse from(FanCreateResult result) {
        return FanCreateResponse.builder().id(result.id()).targetId(result.targetId()).build();
    }
}
