package com.tracek.domain.auth.application.dto.result;

import java.time.OffsetDateTime;
import lombok.Builder;

@Builder
public record OAuthUserDataResult(
        Long providerId,
        String providerName,
        OffsetDateTime connectedAt,
        String nickName,
        String profileImageUrl) {}
