package com.tracek.global.security.authentication;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

class AuditorAwareImplTest {

    private final AuditorAwareImpl auditorAware = new AuditorAwareImpl();

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("인증된 사용자가 있으면 현재 사용자 이름을 반환한다")
    void getCurrentAuditor_authenticatedUser_returnsUsername() {
        // given
        long userId = 1L;

        AuthenticationPrincipal principal =
                new AuthenticationPrincipal(userId, "test", "ROLE_ADMIN");
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.authenticated(principal, null, List.of());

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        var currentAuditor = auditorAware.getCurrentAuditor();

        assertThat(currentAuditor).isPresent().contains(userId);
    }

    @Test
    @DisplayName("인증 정보가 없으면 빈 값을 반환한다")
    void getCurrentAuditor_noAuthentication_returnsEmpty() {
        // given
        SecurityContextHolder.clearContext();

        // when
        var currentAuditor = auditorAware.getCurrentAuditor();

        // then
        assertThat(currentAuditor).isEmpty();
    }

    @Test
    @DisplayName("인증되지 않은 사용자는 빈 값을 반환한다")
    void getCurrentAuditor_unauthenticatedUser_returnsEmpty() {
        // given
        UsernamePasswordAuthenticationToken authentication =
                UsernamePasswordAuthenticationToken.unauthenticated(null, null);

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        // when
        var currentAuditor = auditorAware.getCurrentAuditor();

        // then
        assertThat(currentAuditor).isEmpty();
    }
}
