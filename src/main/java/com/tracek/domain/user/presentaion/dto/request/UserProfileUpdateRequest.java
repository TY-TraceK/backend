package com.tracek.domain.user.presentaion.dto.request;

import com.tracek.domain.user.application.dto.command.UserProfileUpdateCommand;
import org.springframework.web.multipart.MultipartFile;

public record UserProfileUpdateRequest(MultipartFile imageSource, String nickName) {

    public UserProfileUpdateCommand toCommand(Long userId) {
        return UserProfileUpdateCommand.builder()
                .userId(userId)
                .imageSource(imageSource)
                .nickName(nickName)
                .build();
    }
}
