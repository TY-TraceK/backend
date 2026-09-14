package com.tracek.domain.auth.presentation.dto.request;

import com.tracek.domain.auth.application.dto.command.OAuthLoginCommand;

public record OAuthLoginRequest(String code, String redirectUri) {

    public OAuthLoginCommand toCommand(String provider) {
        return OAuthLoginCommand.builder()
                .code(code)
                .redirectUri(redirectUri)
                .provider(provider)
                .build();
    }
}
