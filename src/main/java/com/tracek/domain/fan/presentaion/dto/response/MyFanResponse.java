package com.tracek.domain.fan.presentaion.dto.response;

import com.tracek.domain.fan.application.dto.result.FanTargetResult;
import com.tracek.domain.fan.application.dto.result.MyFanResult;
import java.util.List;
import lombok.Builder;

@Builder
public record MyFanResponse(
        List<FanDataIndividualResponse> artist, List<FanDataIndividualResponse> content) {

    public static MyFanResponse from(MyFanResult result) {
        return MyFanResponse.builder()
                .artist(result.artists().stream().map(FanDataIndividualResponse::from).toList())
                .content(result.contents().stream().map(FanDataIndividualResponse::from).toList())
                .build();
    }
}

@Builder
record FanDataIndividualResponse(
        Long id,
        String name,
        String description,
        String pictureUrl,
        Long fanCount,
        Long totalVerificationCount) {

    public static FanDataIndividualResponse from(FanTargetResult result) {
        return FanDataIndividualResponse.builder()
                .id(result.id())
                .name(result.name())
                .description(result.description())
                .pictureUrl(result.pictureUrl())
                .fanCount(result.fanCount())
                .totalVerificationCount(result.totalVerificationCount())
                .build();
    }
}
