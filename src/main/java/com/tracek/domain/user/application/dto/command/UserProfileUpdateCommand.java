package com.tracek.domain.user.application.dto.command;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record UserProfileUpdateCommand(Long userId, MultipartFile imageSource, String nickName) {}
