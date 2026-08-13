package com.tracek.domain.user.application.service.impl;

import com.tracek.domain.user.application.dto.result.SyncUserResult;
import com.tracek.domain.user.application.dto.result.UserProfileDataResult;
import com.tracek.domain.user.application.service.UserQueryService;
import com.tracek.domain.user.domain.enums.UserStatus;
import com.tracek.domain.user.domain.exception.UserErrorCode;
import com.tracek.domain.user.domain.repository.UserRepository;
import com.tracek.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {

    private final UserRepository userRepository;

    @Override
    public boolean existsUserById(Long userId) {
        return userRepository.findById(userId).isPresent();
    }

    @Override
    public boolean isActiveUser(Long userId) {
        return userRepository.findByIdAndUserStatusIs(userId, UserStatus.ACTIVE);
    }

    @Override
    public UserProfileDataResult getUserProfileData(Long userId) {
        return UserProfileDataResult.from(
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND)));
    }

    @Override
    public SyncUserResult getUserSummaryData(Long userId) {
        return SyncUserResult.from(
                userRepository
                        .findById(userId)
                        .orElseThrow(() -> new CustomException(UserErrorCode.USER_NOT_FOUND)),
                false);
    }
}
