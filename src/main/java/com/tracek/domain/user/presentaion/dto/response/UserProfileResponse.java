package com.tracek.domain.user.presentaion.dto.response;

import com.tracek.domain.user.application.dto.result.UserProfileDataResult;
import lombok.Builder;

@Builder
public record UserProfileResponse(Long id, String nickName, String imageUrl) {

    public static UserProfileResponse from(UserProfileDataResult result) {
        return UserProfileResponse.builder()
                .nickName(result.nickname())
                .imageUrl(result.profileImageUrl())
                .id(result.userId())
                .build();
    }
}
