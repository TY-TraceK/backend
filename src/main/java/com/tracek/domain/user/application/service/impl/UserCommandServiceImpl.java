package com.tracek.domain.user.application.service.impl;

import com.tracek.domain.user.application.dto.command.SyncUserCommand;
import com.tracek.domain.user.application.service.SyncUserResult;
import com.tracek.domain.user.application.service.UserCommandService;
import com.tracek.domain.user.domain.enums.OAuthProvider;
import com.tracek.domain.user.domain.model.OAuthInfo;
import com.tracek.domain.user.domain.model.User;
import com.tracek.domain.user.domain.model.UserProfile;
import com.tracek.domain.user.domain.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;

    @Transactional
    @Override
    public SyncUserResult registerOrUpdateUser(SyncUserCommand command) {
        OAuthInfo oAuthInfo =
                OAuthInfo.register(
                        command.providerId(), OAuthProvider.valueOf(command.providerName()));
        UserProfile userProfile =
                UserProfile.register(command.userNickName(), command.userProfileImageUrl());
        User user = userRepository.findByOAuthInfo(oAuthInfo).orElse(null);
        if (user == null) {
            user = User.createUser(oAuthInfo, command.connectedAt(), userProfile);
            user = userRepository.save(user);
        } else {
            user.setUserProfile(userProfile);
        }
        return SyncUserResult.from(user);
    }
}
