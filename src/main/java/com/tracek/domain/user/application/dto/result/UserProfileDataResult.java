package com.tracek.domain.user.application.dto.result;

import com.tracek.domain.user.domain.model.User;
import lombok.Builder;

@Builder
public record UserProfileDataResult(Long userId, String nickname, String profileImageUrl) {

    public static UserProfileDataResult from(User user) {
        return UserProfileDataResult.builder()
                .userId(user.getId())
                .nickname(user.getUserProfile().getNickname())
                .profileImageUrl(user.getUserProfile().getProfileImageUrl())
                .build();
    }
}
