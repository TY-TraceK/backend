package com.tracek.domain.auth.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

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
import java.time.OffsetDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OAuthServiceImplTest {

    @InjectMocks private OAuthServiceImpl oAuthService;

    @Mock private OAuthClientProvider oAuthClientProvider;

    @Mock private UserCommandService userCommandService;

    @Mock private JwtTokenProvider jwtTokenProvider;

    @Mock private OAuthClient oAuthClient;

    @Nested
    @DisplayName("소셜 로그인 성공 테스트")
    class SuccessTest {

        @Test
        @DisplayName("올바른 인가 코드와 제공자 이름이 전달되면 로그인에 성공하고 토큰을 반환한다")
        void createOauthLogin_Success() {
            // given
            String code = "authorization_code_example";
            String provider = "KAKAO";
            String oAuthAccessToken = "kakao_access_token";

            OAuthUserDataResult userDataResult =
                    new OAuthUserDataResult(
                            1L,
                            "KAKAO",
                            OffsetDateTime.now(),
                            "송유진",
                            "https://example.com/profile.jpg");

            SyncUserResult syncUserResult = new SyncUserResult(1L, true, "송유진", "ROLE_USER");

            given(oAuthClientProvider.getClient(OAuthProvider.KAKAO)).willReturn(oAuthClient);
            given(oAuthClient.exchangeAuthorizationCode(code)).willReturn(oAuthAccessToken);
            given(oAuthClient.getOAuthUserData(oAuthAccessToken)).willReturn(userDataResult);
            given(userCommandService.registerOrUpdateUser(any(SyncUserCommand.class)))
                    .willReturn(syncUserResult);
            given(jwtTokenProvider.createAccessToken(1L, "ROLE_USER", "송유진"))
                    .willReturn("jwt_access_token");
            given(jwtTokenProvider.createRefreshToken(1L)).willReturn("jwt_refresh_token");

            // when
            OAuthLoginResult result = oAuthService.createOauthLogin(code, provider);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(1L);
            assertThat(result.accessToken()).isEqualTo("jwt_access_token");
            assertThat(result.refreshToken()).isEqualTo("jwt_refresh_token");
            assertThat(result.isNewUser()).isTrue();
            assertThat(result.nickName()).isEqualTo("송유진");

            verify(userCommandService).registerOrUpdateUser(any(SyncUserCommand.class));
            verify(jwtTokenProvider).createAccessToken(1L, "ROLE_USER", "송유진");
            verify(jwtTokenProvider).createRefreshToken(1L);
        }
    }

    @Nested
    @DisplayName("소셜 로그인 실패 테스트")
    class FailTest {

        @Test
        @DisplayName("지원하지 않거나 존재하지 않는 Provider인 경우 예외가 발생한다")
        void createOauthLogin_ProviderNotFound() {
            // given
            String code = "authorization_code_example";
            String provider = "KAKAO";

            given(oAuthClientProvider.getClient(OAuthProvider.KAKAO)).willReturn(null);

            // when & then
            assertThatThrownBy(() -> oAuthService.createOauthLogin(code, provider))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(AuthErrorCode.PROVIDER_NOT_FOUND);
        }
    }
}
