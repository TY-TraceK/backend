package com.tracek.domain.auth.application.client;

import com.tracek.domain.auth.application.dto.result.OAuthUserDataResult;
import com.tracek.domain.auth.domain.enums.OAuthProvider;
import org.springframework.stereotype.Component;

@Component
public interface OAuthClient {

    String exchangeAuthorizationCode(String authorizationCode);

    OAuthUserDataResult getOAuthUserData(String kakaoAccessToken);

    OAuthProvider support();
}
