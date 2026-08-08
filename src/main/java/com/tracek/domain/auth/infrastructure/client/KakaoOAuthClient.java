package com.tracek.domain.auth.infrastructure.client;

import com.tracek.domain.auth.application.client.OAuthClient;
import com.tracek.domain.auth.application.dto.result.OAuthUserDataResult;
import com.tracek.domain.auth.domain.enums.OAuthProvider;
import com.tracek.domain.auth.domain.exception.AuthErrorCode;
import com.tracek.domain.auth.infrastructure.config.KakaoOAuthProperties;
import com.tracek.domain.auth.infrastructure.dto.KakaoTokenResponse;
import com.tracek.domain.auth.infrastructure.dto.KakaoUserResponse;
import com.tracek.global.exception.CustomException;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class KakaoOAuthClient implements OAuthClient {

    private final RestClient authorizationRestClient;
    private final RestClient apiRestClient;
    private final KakaoOAuthProperties properties;

    public KakaoOAuthClient(
            @Qualifier("kakaoAuthorizationRestClient") RestClient authorizationRestClient,
            @Qualifier("kakaoApiRestClient") RestClient apiRestClient,
            KakaoOAuthProperties properties) {
        this.authorizationRestClient = authorizationRestClient;
        this.apiRestClient = apiRestClient;
        this.properties = properties;
    }

    @Override
    public String exchangeAuthorizationCode(String authorizationCode) {
        try {
            MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
            form.add("grant_type", "authorization_code");
            form.add("client_id", properties.clientId());
            form.add("client_secret", properties.clientSecret());
            form.add("redirect_uri", properties.redirectUri());
            form.add("code", authorizationCode);

            KakaoTokenResponse response =
                    authorizationRestClient
                            .post()
                            .uri("/oauth/token")
                            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                            .body(form)
                            .retrieve()
                            .body(KakaoTokenResponse.class);

            if (response == null || isBlank(response.accessToken())) {
                throw new Exception();
            }
            return response.accessToken();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new CustomException(AuthErrorCode.OAUTH_CODE_INVALID);
        }
    }

    @Override
    public OAuthUserDataResult getOAuthUserData(String oauthAccessToken) {
        try {

            KakaoUserResponse response =
                    apiRestClient
                            .get()
                            .uri("/v2/user/me")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + oauthAccessToken)
                            .accept(MediaType.APPLICATION_JSON)
                            .retrieve()
                            .body(KakaoUserResponse.class);

            if (response == null || response.id() == null) {
                throw new Exception();
            }
            KakaoUserResponse.Profile profile =
                    Objects.requireNonNullElseGet(
                                    response.kakaoAccount(),
                                    () -> new KakaoUserResponse.KakaoAccount(null))
                            .profile();
            if (profile == null || isBlank(profile.nickname())) {
                throw new Exception();
            }
            return OAuthUserDataResult.builder()
                    .providerId(response.id())
                    .providerName("KAKAO")
                    .nickName(profile.nickname())
                    .profileImageUrl(profile.profileImageUrl())
                    .connectedAt(response.connectedAt())
                    .build();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
            throw new CustomException(AuthErrorCode.OAUTH_USER_INVALID);
        }
    }

    @Override
    public OAuthProvider support() {
        return OAuthProvider.KAKAO;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
