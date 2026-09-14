package com.tracek.domain.auth.application.dto.command;

import lombok.Builder;

@Builder
public record OAuthLoginCommand(String code, String provider, String redirectUri) {}
