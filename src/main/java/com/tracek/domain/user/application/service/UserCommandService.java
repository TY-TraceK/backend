package com.tracek.domain.user.application.service;

import com.tracek.domain.user.application.dto.command.SyncUserCommand;
import com.tracek.domain.user.application.dto.result.SyncUserResult;

public interface UserCommandService {

    SyncUserResult registerOrUpdateUser(SyncUserCommand command);
}
