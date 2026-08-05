package com.tracek.global.security.jwt;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tracek.global.exception.CustomException;
import com.tracek.global.response.SecurityErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtProvider;
    private JwtProperties jwtProperties;

    @BeforeEach
    void setUp() {
        jwtProperties =
                new JwtProperties(
                        "test-secret-key-test-secret-key-123456789", 1_800_000L, 1_209_600_000L);

        jwtProvider = new JwtTokenProvider(jwtProperties);
        jwtProvider.init();
    }

    private char changeLastCharacter(String token) {
        char lastCharacter = token.charAt(token.length() - 1);

        return lastCharacter == 'a' ? 'b' : 'a';
    }

    @Nested
    @DisplayName("Access Token 생성")
    class CreateAccessToken {

        @Test
        @DisplayName("사용자 정보로 Access Token이 생성된다")
        void createAccessToken() {
            // given
            Long userId = 1L;
            String role = "ROLE_USER";
            String userName = "TEST_USER";

            // when
            String token = jwtProvider.createAccessToken(userId, role, userName);

            // then
            assertAll(
                    () -> assertNotNull(token),
                    () -> assertFalse(token.isBlank()),
                    () -> assertEquals(3, token.split("\\.").length));
        }

        @Test
        @DisplayName("생성된 Access Token에는 사용자 ID가 포함된다.")
        void accessTokenContainsUserId() {
            // given
            Long userId = 1L;
            String role = "ROLE_USER";
            String userName = "TEST_USER";

            String token = jwtProvider.createAccessToken(userId, role, userName);

            // when
            Long extractedUserId = jwtProvider.getUserId(token);

            // then
            assertEquals(userId, extractedUserId);
        }

        @Test
        @DisplayName("생성된 Access Token에는 사용자 역할, 이름이 포함된다.")
        void accessTokenContainsRole() {
            // given
            Long userId = 1L;
            String role = "ROLE_USER";
            String userName = "TEST_USER";

            String token = jwtProvider.createAccessToken(userId, role, userName);

            // when
            Claims extractedClaims = jwtProvider.getClaims(token);

            // then
            assertEquals(role, extractedClaims.get("role"));
            assertEquals(userName, extractedClaims.get("name"));
        }
    }

    @Nested
    @DisplayName("JWT 검증")
    class ValidateToken {

        @Test
        @DisplayName("정상적으로 생성된 토큰은 유효하다")
        void validToken() {
            // given
            String token = jwtProvider.createAccessToken(1L, "ROLE_USER", "TEST_USER");

            // when
            boolean result = jwtProvider.validateToken(token);

            // then
            assertTrue(result);
        }

        @Test
        @DisplayName("변조된 토큰은 유효하지 않다")
        void tamperedToken() {
            // given
            String token = jwtProvider.createAccessToken(1L, "ROLE_USER", "TEST_USER");

            String tamperedToken =
                    token.substring(0, token.length() - 1) + changeLastCharacter(token);

            CustomException exception =
                    assertThrows(
                            CustomException.class, () -> jwtProvider.validateToken(tamperedToken));

            // then
            assertEquals(SecurityErrorCode.INVALID_SIGNATURE, exception.getErrorCode());
        }

        @Test
        @DisplayName("JWT 형식이 아닌 문자열은 유효하지 않다")
        void malformedToken() {
            // given
            String invalidToken = "invalid-token";

            CustomException exception =
                    assertThrows(
                            CustomException.class, () -> jwtProvider.validateToken(invalidToken));

            // then
            assertEquals(SecurityErrorCode.INVALID_SIGNATURE, exception.getErrorCode());
        }

        @Test
        @DisplayName("만료된 토큰이면 TOKEN_EXPIRED 예외가 발생한다")
        void validateToken_expiredToken_throwsException() {
            // given
            JwtProperties expiredProperties =
                    new JwtProperties(
                            "test-secret-key-test-secret-key-123456789",
                            -1_000L, // 발급 시점보다 1초 전에 만료
                            1_209_600_000L);

            JwtTokenProvider expiredJwtProvider = new JwtTokenProvider(expiredProperties);

            expiredJwtProvider.init();

            String expiredToken =
                    expiredJwtProvider.createAccessToken(1L, "ROLE_USER", "TEST_USER");

            // when
            CustomException exception =
                    assertThrows(
                            CustomException.class,
                            () -> expiredJwtProvider.validateToken(expiredToken));

            // then
            assertEquals(SecurityErrorCode.EXPIRED_TOKEN, exception.getErrorCode());
        }

        @Test
        @DisplayName("빈 토큰은 유효하지 않다")
        void blankToken() {
            // when
            CustomException exception =
                    assertThrows(CustomException.class, () -> jwtProvider.validateToken(""));

            // then
            assertEquals(SecurityErrorCode.EMPTY_CLAIMS, exception.getErrorCode());
        }
    }

    @Nested
    @DisplayName("Refresh Token 생성")
    class CreateRefreshToken {

        @Test
        @DisplayName("사용자 UUID로 Refresh Token이 생성된다")
        void createRefreshToken_success() {
            // given
            long userId = 1;
            // when
            String refreshToken = jwtProvider.createRefreshToken(userId);

            // then
            assertAll(
                    () -> assertNotNull(refreshToken),
                    () -> assertFalse(refreshToken.isBlank()),
                    () -> assertEquals(3, refreshToken.split("\\.").length));
        }

        @Test
        @DisplayName("Refresh Token의 subject에는 사용자 UUID가 저장된다")
        void createRefreshToken_containsUserId() {
            // given
            long userId = 1;

            // when
            String refreshToken = jwtProvider.createRefreshToken(userId);

            Claims claims = jwtProvider.getClaims(refreshToken);

            // then
            assertEquals(userId, Long.parseLong(claims.getSubject()));
        }

        @Test
        @DisplayName("Refresh Token에는 만료 시간이 설정된다")
        void createRefreshToken_containsExpiration() {
            // given
            long userId = 1;

            // when
            String refreshToken = jwtProvider.createRefreshToken(userId);

            Claims claims = jwtProvider.getClaims(refreshToken);

            // then
            assertAll(
                    () -> assertNotNull(claims.getIssuedAt()),
                    () -> assertNotNull(claims.getExpiration()),
                    () -> assertTrue(claims.getExpiration().after(claims.getIssuedAt())));
        }
    }

    @Nested
    @DisplayName("토큰 잔여 시간 조회")
    class GetRemainingTime {

        @Test
        @DisplayName("유효한 토큰이면 남은 만료 시간을 반환한다")
        void getRemainingTime_validToken_returnsRemainingTime() {

            // given
            long userId = 1;

            String refreshToken = jwtProvider.createRefreshToken(userId);

            // when
            long remainingTime = jwtProvider.getRemainingTime(refreshToken);

            // then
            assertAll(
                    () -> assertTrue(remainingTime > 0),
                    () -> assertTrue(remainingTime <= jwtProperties.getRefreshExpirationTime()));
        }

        @Test
        @DisplayName("만료된 토큰이면 0을 반환한다")
        void getRemainingTime_expiredToken_returnsZero() {
            // given
            JwtProperties expiredProperties =
                    new JwtProperties(
                            "test-secret-key-test-secret-key-123456789", -1_000L, -1_209_600_000L);

            JwtTokenProvider expiredJwtProvider = new JwtTokenProvider(expiredProperties);

            expiredJwtProvider.init();

            String expiredToken = expiredJwtProvider.createRefreshToken(1L);

            // when
            long remainingTime = jwtProvider.getRemainingTime(expiredToken);

            // then
            assertEquals(0L, remainingTime);
        }

        @Test
        @DisplayName("잘못된 형식의 토큰이면 0을 반환한다")
        void getRemainingTime_invalidToken_returnsZero() {
            // given
            String invalidToken = "invalid-token";

            // when
            long remainingTime = jwtProvider.getRemainingTime(invalidToken);

            // then
            assertEquals(0L, remainingTime);
        }

        @Test
        @DisplayName("서명되지 않은 토큰이면 지원하지 않는 JWT 예외가 발생한다")
        void validateToken_unsignedToken_throwsUnsupportedJwtException() {
            // given
            String unsignedToken =
                    Jwts.builder()
                            .subject("1")
                            .claim("role", "ROLE_USER")
                            .claim("userName", "TEST_USER")
                            .compact();

            // when
            CustomException exception =
                    assertThrows(
                            CustomException.class, () -> jwtProvider.validateToken(unsignedToken));

            // then
            assertEquals(SecurityErrorCode.UNSUPPORTED_TOKEN, exception.getErrorCode());
        }

        @Test
        @DisplayName("빈 토큰이면 0을 반환한다")
        void getRemainingTime_blankToken_returnsZero() {
            // given
            String blankToken = "";

            // when
            long remainingTime = jwtProvider.getRemainingTime(blankToken);

            // then
            assertEquals(0L, remainingTime);
        }
    }
}
