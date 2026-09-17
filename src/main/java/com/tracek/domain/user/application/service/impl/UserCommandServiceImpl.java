package com.tracek.domain.user.application.service.impl;

import com.tracek.domain.user.application.client.StorageClient;
import com.tracek.domain.user.application.dto.command.SyncUserCommand;
import com.tracek.domain.user.application.dto.command.UserProfileUpdateCommand;
import com.tracek.domain.user.application.dto.result.SyncUserResult;
import com.tracek.domain.user.application.dto.result.UserProfileDataResult;
import com.tracek.domain.user.application.service.UserCommandService;
import com.tracek.domain.user.domain.enums.OAuthProvider;
import com.tracek.domain.user.domain.exception.UserErrorCode;
import com.tracek.domain.user.domain.model.OAuthInfo;
import com.tracek.domain.user.domain.model.User;
import com.tracek.domain.user.domain.model.UserProfile;
import com.tracek.domain.user.domain.repository.UserRepository;
import com.tracek.global.exception.CustomException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final StorageClient storageClient;

    @Transactional
    @Override
    public SyncUserResult registerOrUpdateUser(SyncUserCommand command) {
        OAuthInfo oAuthInfo =
                OAuthInfo.register(
                        command.providerId(), OAuthProvider.valueOf(command.providerName()));
        UserProfile userProfile =
                UserProfile.register(command.userNickName(), command.userProfileImageUrl());
        User user = userRepository.findByOAuthInfo(oAuthInfo).orElse(null);
        boolean isNewUser = false;
        if (user == null) {
            user = User.createUser(oAuthInfo, command.connectedAt(), userProfile);
            user = userRepository.save(user);
            isNewUser = true;
        } else {
            user.setUserProfile(userProfile);
        }
        return SyncUserResult.from(user, isNewUser);
    }

    private String uploadImage(Long userId, MultipartFile imageSource) {
        String fileName = "user_image_" + userId + ".jpg";
        return storageClient.upload(imageSource, fileName);
    }

    @Override
    @Transactional
    public UserProfileDataResult updateUserProfile(UserProfileUpdateCommand command) {
        String uploadUrl = uploadImage(command.userId(), command.imageSource());
        User user =
                userRepository
                        .findById(command.userId())
                        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND));
        user.updateUserProfile(UserProfile.register(uploadUrl, command.nickName()));
        return UserProfileDataResult.from(user);
    }
}
