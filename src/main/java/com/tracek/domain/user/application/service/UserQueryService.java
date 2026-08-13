package com.tracek.domain.user.application.service;

import com.tracek.domain.user.application.dto.result.UserProfileDataResult;

public interface UserQueryService {

    boolean existsUserById(Long userId);

    boolean isActiveUser(Long userId);

    UserProfileDataResult getUserProfileData(Long userId);

    SyncUserResult getUserSummaryData(Long userId);
}
