package com.tracek.domain.user.application.service;

import com.tracek.domain.user.domain.model.User;
import lombok.Builder;

@Builder
public record SyncUserResult(long userId, boolean isNewUser, String userName, String userRole) {

  public static SyncUserResult from(User user, boolean isNewUser) {
    return SyncUserResult.builder()
        .userId(user.getId())
        .isNewUser(isNewUser)
        .userName(user.getUserProfile().getNickname())
        .userName(user.getUserRole().name())
        .build();
  }
}
