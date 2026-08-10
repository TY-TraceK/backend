package com.tracek.domain.auth.presentation.controller;

import com.tracek.domain.auth.application.service.OAuthService;
import com.tracek.domain.auth.presentation.controller.docs.AuthControllerDocs;
import com.tracek.domain.auth.presentation.dto.response.OAuthLoginResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthControllerDocs {

    private final OAuthService oAuthService;

    @Override
    @PostMapping("/{provider}")
    public ApiResponse<OAuthLoginResponse> createOauthLogin(String code, String provider) {
        return ApiResponse.success(
                GeneralSuccessCode.OK,
                OAuthLoginResponse.from(oAuthService.createOauthLogin(code, provider)));
    }
}
