package com.tracek.domain.fan.application.dto.result;

import lombok.Builder;

@Builder
public record ArtistFanViewResult(long fanCount, boolean isFan) {

    public static ArtistFanViewResult of(long fanCount, boolean isFan) {
        return ArtistFanViewResult.builder().fanCount(fanCount).isFan(isFan).build();
    }
}
