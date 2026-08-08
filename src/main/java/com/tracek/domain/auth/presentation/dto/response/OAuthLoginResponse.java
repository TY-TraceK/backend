package com.tracek.domain.auth.presentation.dto.response;

import com.tracek.domain.auth.application.dto.result.OAuthLoginResult;
import lombok.Builder;

@Builder
public record OAuthLoginResponse(
        Long userId,
        String accessToken,
        String refreshToken,
        Boolean isNewUser,
        String nickName,
        String profileImageUrl) {

    public static OAuthLoginResponse from(OAuthLoginResult result) {
        return OAuthLoginResponse.builder()
                .userId(result.userId())
                .accessToken(result.accessToken())
                .refreshToken(result.refreshToken())
                .isNewUser(result.isNewUser())
                .nickName(result.nickName())
                .profileImageUrl(result.profileImageUrl())
                .build();
    }
}
