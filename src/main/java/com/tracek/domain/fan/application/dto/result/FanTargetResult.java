package com.tracek.domain.fan.application.dto.result;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.content.application.dto.ContentResult;
import lombok.Builder;

@Builder
public record FanTargetResult(
        Long id,
        String name,
        String description,
        String pictureUrl,
        Long fanCount,
        Long totalVerificationCount) {

    public static FanTargetResult from(ContentResult result) {
        return FanTargetResult.builder()
                .id(result.getContentId())
                .name(result.getTitle())
                .description(result.getDescription())
                .pictureUrl(result.getPictureUrl())
                .fanCount(result.getFanCount())
                .totalVerificationCount(result.getTotalVerificationCount())
                .build();
    }

    public static FanTargetResult from(ArtistResult result) {
        return FanTargetResult.builder()
                .id(result.getId())
                .name(result.getName())
                .description(result.getAlias())
                .pictureUrl(result.getPictureUrl())
                .fanCount(result.getFanCount())
                .totalVerificationCount(result.getTotalVerificationCount())
                .build();
    }
}
