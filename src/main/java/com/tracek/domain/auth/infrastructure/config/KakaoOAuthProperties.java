package com.tracek.domain.auth.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.auth.kakao")
public record KakaoOAuthProperties(
        String clientId,
        String clientSecret,
        String redirectUri,
        String authorizationBaseUrl,
        String apiBaseUrl) {

    private static final String DEFAULT_AUTHORIZATION_BASE_URL = "https://kauth.kakao.com";
    private static final String DEFAULT_API_BASE_URL = "https://kapi.kakao.com";

    public KakaoOAuthProperties {
        authorizationBaseUrl = defaultIfBlank(authorizationBaseUrl, DEFAULT_AUTHORIZATION_BASE_URL);
        apiBaseUrl = defaultIfBlank(apiBaseUrl, DEFAULT_API_BASE_URL);
    }

    private static String defaultIfBlank(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
