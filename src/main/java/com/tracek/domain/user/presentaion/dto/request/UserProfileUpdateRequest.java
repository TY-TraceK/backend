package com.tracek.domain.user.presentaion.dto.request;

import com.tracek.domain.user.application.dto.command.UserProfileUpdateCommand;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record UserProfileUpdateRequest(
        @NotNull MultipartFile imageSource, @NotNull String nickName) {

    public UserProfileUpdateCommand toCommand(Long userId) {
        return UserProfileUpdateCommand.builder()
                .userId(userId)
                .imageSource(imageSource)
                .nickName(nickName)
                .build();
    }
}
