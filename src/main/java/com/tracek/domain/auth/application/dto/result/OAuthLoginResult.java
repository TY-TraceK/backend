package com.tracek.domain.auth.application.dto.result;

import lombok.Builder;

@Builder
public record OAuthLoginResult(
        Long userId,
        String accessToken,
        String refreshToken,
        Boolean isNewUser,
        String nickName,
        String profileImageUrl) {

    public static OAuthLoginResult of(
            Long userId,
            String accessToken,
            String refreshToken,
            Boolean isNewUser,
            String nickName,
            String profileImageUrl) {
        return OAuthLoginResult.builder()
                .userId(userId)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .isNewUser(isNewUser)
                .nickName(nickName)
                .profileImageUrl(profileImageUrl)
                .build();
    }
}
