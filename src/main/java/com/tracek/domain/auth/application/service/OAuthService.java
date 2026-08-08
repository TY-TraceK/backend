package com.tracek.domain.auth.application.service;

import com.tracek.domain.auth.application.dto.result.OAuthLoginResult;

public interface OAuthService {

    OAuthLoginResult createOauthLogin(String code, String provider);
}
