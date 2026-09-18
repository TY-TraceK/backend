package com.tracek.domain.user.presentaion.controller;

import com.tracek.domain.user.application.dto.result.UserActivityProjectionResult;
import lombok.Builder;

@Builder
public record UserActivityProjectionResponse(
        Long fanCount, Long visitVerificationCount, Long likedCount, Long bookMarkCount) {

    public static UserActivityProjectionResponse from(UserActivityProjectionResult result) {
        return UserActivityProjectionResponse.builder()
                .fanCount(result.fanCount())
                .visitVerificationCount(result.visitVerificationCount())
                .likedCount(result.likedCount())
                .bookMarkCount(result.bookMarkCount())
                .build();
    }
}
