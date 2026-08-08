package com.tracek.domain.auth.application.service;

import com.tracek.domain.auth.application.client.OAuthClient;
import com.tracek.domain.auth.application.dto.result.OAuthLoginResult;
import com.tracek.domain.auth.application.dto.result.OAuthUserDataResult;
import com.tracek.domain.auth.application.provider.OAuthClientProvider;
import com.tracek.domain.auth.domain.enums.OAuthProvider;
import com.tracek.domain.auth.domain.exception.AuthErrorCode;
import com.tracek.domain.user.application.dto.command.SyncUserCommand;
import com.tracek.domain.user.application.service.SyncUserResult;
import com.tracek.domain.user.application.service.UserCommandService;
import com.tracek.global.exception.CustomException;
import com.tracek.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthServiceImpl implements OAuthService {

    private final OAuthClientProvider oAuthClientProvider;
    private final UserCommandService userCommandService;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public OAuthLoginResult createOauthLogin(String code, String providerName) {

        OAuthUserDataResult dataResult = connectAndGetOauthUser(code, providerName);
        SyncUserResult userResult =
                userCommandService.registerOrUpdateUser(
                        SyncUserCommand.builder()
                                .providerId(dataResult.providerId())
                                .connectedAt(dataResult.connectedAt())
                                .providerName(dataResult.providerName())
                                .userNickName(dataResult.nickName())
                                .userProfileImageUrl(dataResult.profileImageUrl())
                                .build());
        String accessToken =
                jwtTokenProvider.createAccessToken(
                        userResult.userId(), userResult.userRole(), userResult.userName());
        String refreshToken = jwtTokenProvider.createRefreshToken(userResult.userId());
        return OAuthLoginResult.of(
                userResult.userId(),
                accessToken,
                refreshToken,
                userResult.isNewUser(),
                dataResult.nickName(),
                dataResult.profileImageUrl());
    }

    private OAuthUserDataResult connectAndGetOauthUser(String code, String providerName) {
        OAuthProvider provider =
                OAuthProvider.from(providerName)
                        .orElseThrow(() -> new CustomException(AuthErrorCode.PROVIDER_NOT_FOUND));
        OAuthClient client = oAuthClientProvider.getClient(provider);
        if (client == null) {
            throw new CustomException(AuthErrorCode.PROVIDER_NOT_FOUND);
        }
        String oAuthAccessToken = client.exchangeAuthorizationCode(code);
        return client.getOAuthUserData(oAuthAccessToken);
    }
}
