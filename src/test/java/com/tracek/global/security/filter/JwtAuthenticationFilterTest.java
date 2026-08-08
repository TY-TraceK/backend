package com.tracek.global.security.filter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.tracek.global.exception.CustomException;
import com.tracek.global.response.SecurityErrorCode;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import com.tracek.global.security.jwt.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock private JwtTokenProvider jwtTokenProvider;

    @Mock private FilterChain filterChain;

    @InjectMocks private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName(
            "유효한 JWT 토큰이 헤더에 포함되면 Authentication이 SecurityContext에 저장되고, Principal에서 id, name, role 등 세부 정보를 가져올 수 있다.")
    void doFilterInternal_ValidToken_Success() throws ServletException, IOException {
        // given
        String token = "valid.jwt.token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer " + token);

        Claims claims = mock(Claims.class);
        given(claims.getSubject()).willReturn("1");
        given(claims.get("role")).willReturn("ROLE_USER");
        given(claims.get("name")).willReturn("유진");

        given(jwtTokenProvider.validateToken(token)).willReturn(true);
        given(jwtTokenProvider.getClaims(token)).willReturn(claims);

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isInstanceOf(AuthenticationPrincipal.class);

        AuthenticationPrincipal principal = (AuthenticationPrincipal) authentication.getPrincipal();

        assertThat(principal.userId()).isEqualTo(1L);
        assertThat(principal.getUsername())
                .isEqualTo("유진"); // UserDetails 인터페이스 구현 메서드 또는 Record 필드

        // GrantedAuthority 권한 목록 검증 (ROLE_USER가 포함되어 있는지)
        assertThat(principal.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");

        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("Authorization 헤더가 없으면 인증 과정을 거치지 않고 다음 필터로 진행한다.")
    void doFilterInternal_NoHeader_PassFilter() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        jwtAuthenticationFilter.doFilter(request, response, filterChain);

        // then
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
    }

    @Test
    @DisplayName("유효하지 않거나 검증 실패한 토큰이면 CustomException을 던진다.")
    void doFilterInternal_InvalidToken_ThrowsCustomException() {
        // given
        String token = "invalid.jwt.token";
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        request.addHeader("Authorization", "Bearer " + token);

        given(jwtTokenProvider.validateToken(token)).willReturn(true);
        given(jwtTokenProvider.getClaims(token))
                .willThrow(new RuntimeException("Token parsing error"));

        // when & then
        assertThatThrownBy(() -> jwtAuthenticationFilter.doFilter(request, response, filterChain))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(SecurityErrorCode.INVALID_TOKEN);
    }
}
