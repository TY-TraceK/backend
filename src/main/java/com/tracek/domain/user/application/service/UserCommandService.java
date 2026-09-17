package com.tracek.domain.user.application.service;

import com.tracek.domain.user.application.dto.command.SyncUserCommand;
import com.tracek.domain.user.application.dto.command.UserProfileUpdateCommand;
import com.tracek.domain.user.application.dto.result.SyncUserResult;
import com.tracek.domain.user.application.dto.result.UserProfileDataResult;

public interface UserCommandService {

    SyncUserResult registerOrUpdateUser(SyncUserCommand command);

    UserProfileDataResult updateUserProfile(UserProfileUpdateCommand command);
}
