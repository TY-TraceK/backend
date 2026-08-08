package com.tracek.domain.user.application.service;

import com.tracek.domain.user.application.dto.command.SyncUserCommand;

public interface UserCommandService {

    SyncUserResult registerOrUpdateUser(SyncUserCommand command);
}
