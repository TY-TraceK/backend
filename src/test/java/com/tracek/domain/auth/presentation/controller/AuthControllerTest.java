package com.tracek.domain.auth.presentation.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracek.domain.auth.application.dto.command.OAuthLoginCommand;
import com.tracek.domain.auth.application.dto.result.OAuthLoginResult;
import com.tracek.domain.auth.application.service.OAuthService;
import com.tracek.domain.auth.presentation.dto.request.OAuthLoginRequest;
import com.tracek.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = AuthController.class,
        excludeFilters = {
            @ComponentScan.Filter(
                    type = FilterType.ASSIGNABLE_TYPE,
                    classes = com.tracek.global.security.filter.JwtAuthenticationFilter.class)
        })
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private OAuthService oAuthService;

    @MockitoBean private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("카카오 소셜 로그인 요청 시 200 OK와 ApiResponse 래퍼 구조로 응답을 반환한다")
    void createOauthLogin_Success() throws Exception {
        // given
        String provider = "kakao";
        String code = "sample_authorization_code";
        String redirectUri = "http://localhost:5173/oauth/callback";

        OAuthLoginRequest request = new OAuthLoginRequest(code, redirectUri);

        OAuthLoginCommand command =
                OAuthLoginCommand.builder()
                        .code(code)
                        .redirectUri(redirectUri)
                        .provider(provider)
                        .build();

        OAuthLoginResult loginResult =
                OAuthLoginResult.of(
                        1L,
                        "access_token_example",
                        "refresh_token_example",
                        true,
                        "송유진",
                        "https://example.com/profile.jpg");

        given(oAuthService.createOauthLogin(command)).willReturn(loginResult);

        // when & then
        mockMvc.perform(
                        post("/api/auth/{provider}", provider)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isSuccess").value(true))
                .andExpect(jsonPath("$.code").value("COMMON_001"))
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.accessToken").value("access_token_example"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh_token_example"))
                .andExpect(jsonPath("$.data.isNewUser").value(true))
                .andExpect(jsonPath("$.data.nickName").value("송유진"))
                .andExpect(
                        jsonPath("$.data.profileImageUrl")
                                .value("https://example.com/profile.jpg"));
    }
}
