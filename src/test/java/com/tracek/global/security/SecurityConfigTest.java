package com.tracek.global.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tracek.global.security.SecurityConfigTest.TestController;
import com.tracek.global.security.filter.JwtAuthenticationFilter;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = TestController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private SecurityFilterChain securityFilterChain;

    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUpJwtAuthenticationFilter() throws Exception {
        doAnswer(
                        invocation -> {
                            HttpServletRequest request = invocation.getArgument(0);
                            HttpServletResponse response = invocation.getArgument(1);
                            FilterChain filterChain = invocation.getArgument(2);

                            filterChain.doFilter(request, response);

                            return null;
                        })
                .when(jwtAuthenticationFilter)
                .doFilter(any(), any(), any());
    }

    // test용 컨트롤러
    @RestController
    static class TestController {

        @GetMapping("/api/public/ping")
        String publicPing() {
            return "public";
        }

        @GetMapping("/api/auth/login")
        String login() {
            return "login";
        }

        @GetMapping("/api/v1/posts/{postId}")
        String getPost(@PathVariable Long postId) {
            return "post-" + postId;
        }

        @PostMapping("/api/v1/posts")
        String createPost() {
            return "created";
        }

        @GetMapping("/api/v1/products/{productId}")
        String getProduct(@PathVariable Long productId) {
            return "product-" + productId;
        }

        @GetMapping("/api/users/me")
        String getMyInformation() {
            return "user";
        }

        @GetMapping("/api/private/ping")
        String privatePing() {
            return "private";
        }
    }

    @Nested
    @DisplayName("공개 API 접근")
    class PublicApi {

        @Test
        @DisplayName("/api/public/**는 인증 없이 접근할 수 있다")
        void publicApi_withoutAuthentication_returnsOk() throws Exception {
            mockMvc.perform(get("/api/public/ping"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("public"));
        }

        @Test
        @DisplayName("/api/auth/**는 인증 없이 접근할 수 있다")
        void authApi_withoutAuthentication_returnsOk() throws Exception {
            mockMvc.perform(get("/api/auth/login"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("login"));
        }

        @Test
        @DisplayName("게시글 GET API는 인증 없이 접근할 수 있다")
        void getPost_withoutAuthentication_returnsOk() throws Exception {
            mockMvc.perform(get("/api/v1/posts/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("post-1"));
        }

        @Test
        @DisplayName("상품 GET API는 인증 없이 접근할 수 있다")
        void getProduct_withoutAuthentication_returnsOk() throws Exception {
            mockMvc.perform(get("/api/v1/products/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("product-1"));
        }
    }

    @Nested
    @DisplayName("인증이 필요한 API 접근")
    class AuthenticatedApi {

        @Test
        @DisplayName("/api/users/**는 인증 없이 접근할 수 없다")
        void usersApi_withoutAuthentication_returnsForbidden() throws Exception {
            mockMvc.perform(get("/api/users/me")).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("/api/users/**는 인증된 사용자가 접근할 수 있다")
        void usersApi_withAuthentication_returnsOk() throws Exception {
            mockMvc.perform(get("/api/users/me").with(user("1").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(content().string("user"));
        }

        @Test
        @DisplayName("별도로 허용되지 않은 API는 인증 없이 접근할 수 없다")
        void privateApi_withoutAuthentication_returnsForbidden() throws Exception {
            mockMvc.perform(get("/api/private/ping")).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("별도로 허용되지 않은 API도 인증 사용자는 접근할 수 있다")
        void privateApi_withAuthentication_returnsOk() throws Exception {
            mockMvc.perform(get("/api/private/ping").with(user("1").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(content().string("private"));
        }
    }

    @Nested
    @DisplayName("HTTP Method별 인가 규칙")
    class HttpMethodAuthorization {

        @Test
        @DisplayName("게시글 GET은 인증 없이 접근할 수 있다")
        void getPost_withoutAuthentication_returnsOk() throws Exception {
            mockMvc.perform(get("/api/v1/posts/1")).andExpect(status().isOk());
        }

        @Test
        @DisplayName("게시글 POST는 인증 없이 접근할 수 없다")
        void createPost_withoutAuthentication_returnsForbidden() throws Exception {
            mockMvc.perform(post("/api/v1/posts")).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("게시글 POST는 인증 사용자가 접근할 수 있다")
        void createPost_withAuthentication_returnsOk() throws Exception {
            mockMvc.perform(post("/api/v1/posts").with(user("1").roles("USER")))
                    .andExpect(status().isOk())
                    .andExpect(content().string("created"));
        }
    }

    @Nested
    @DisplayName("JWT 필터 등록")
    class JwtFilterRegistration {

        @Test
        @DisplayName("JwtAuthenticationFilter가 SecurityFilterChain에 등록된다")
        void jwtFilter_isRegistered() {
            // when
            List<Filter> filters = securityFilterChain.getFilters();

            // then
            assertThat(filters).contains(jwtAuthenticationFilter);
        }

        @Test
        @DisplayName("JwtAuthenticationFilter는 인가 필터보다 먼저 실행되도록 등록된다")
        void jwtFilter_isBeforeAuthorizationFilter() {
            // given
            List<Filter> filters = securityFilterChain.getFilters();

            int jwtFilterIndex = filters.indexOf(jwtAuthenticationFilter);

            int authorizationFilterIndex =
                    IntStream.range(0, filters.size())
                            .filter(index -> filters.get(index) instanceof AuthorizationFilter)
                            .findFirst()
                            .orElseThrow();

            // then
            assertThat(jwtFilterIndex)
                    .isGreaterThanOrEqualTo(0)
                    .isLessThan(authorizationFilterIndex);
        }

        @Test
        @DisplayName("HTTP 요청이 들어오면 JwtAuthenticationFilter가 실행된다")
        void request_executesJwtAuthenticationFilter() throws Exception {
            // when
            mockMvc.perform(get("/api/public/ping")).andExpect(status().isOk());

            // then
            verify(jwtAuthenticationFilter, atLeastOnce()).doFilter(any(), any(), any());
        }
    }
}
