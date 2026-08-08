package com.tracek.domain.auth.infrastructure.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.OffsetDateTime;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoUserResponse(Long id, OffsetDateTime connectedAt, KakaoAccount kakaoAccount) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record KakaoAccount(Profile profile) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Profile(String nickname, String profileImageUrl) {}
}
