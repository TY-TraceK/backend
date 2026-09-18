package com.tracek.domain.auth.application.service;

import com.tracek.domain.auth.application.dto.command.OAuthLoginCommand;
import com.tracek.domain.auth.application.dto.result.OAuthLoginResult;

public interface OAuthService {

    OAuthLoginResult createOauthLogin(OAuthLoginCommand command);

    OAuthLoginResult refreshTokens(String refreshToken);

    String getUserAndAccessToken(Long userId);
}
