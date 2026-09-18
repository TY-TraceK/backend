package com.tracek.domain.auth.presentation.controller.docs;

import com.tracek.domain.auth.presentation.dto.request.OAuthLoginRequest;
import com.tracek.domain.auth.presentation.dto.request.TokenRefreshRequest;
import com.tracek.domain.auth.presentation.dto.response.OAuthLoginResponse;
import com.tracek.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "AUTH", description = "인증/인가")
public interface AuthControllerDocs {

    @Operation(
            summary = "토큰 재발급",
            description = "유효한 Refresh Token으로 Access/Refresh Token을 재발급합니다.")
    ApiResponse<OAuthLoginResponse> refreshTokens(
            @Parameter(description = "Refresh Token", required = true) @RequestBody @Valid
                    TokenRefreshRequest request);

    @Operation(summary = "oauth 회원가입", description = "oauth provider에 따라 회원가입 또는 로그인을 제공합니다. ")
    ApiResponse<OAuthLoginResponse> createOauthLogin(
            @Parameter(description = "Oauth request", required = true) @RequestBody @Valid
                    OAuthLoginRequest request,
            @Parameter(description = "Oauth 종류", required = true) @PathVariable String provider);
}
