package com.tracek.domain.fan.application.dto.result;

import lombok.Builder;

@Builder
public record ContentFanViewResult(long fanCount, boolean isFan) {

    public static ContentFanViewResult of(long fanCount, boolean isFan) {
        return ContentFanViewResult.builder().fanCount(fanCount).isFan(isFan).build();
    }
}
