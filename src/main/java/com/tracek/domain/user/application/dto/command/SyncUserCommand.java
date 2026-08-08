package com.tracek.domain.user.application.dto.command;

import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record SyncUserCommand(
        long providerId,
        String providerName,
        String userNickName,
        String userProfileImageUrl,
        OffsetDateTime connectedAt) {}
