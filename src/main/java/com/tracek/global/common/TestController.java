package com.tracek.global.common;

import com.tracek.domain.auth.application.service.OAuthService;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Profile({"dev", "local"})
@RequestMapping("/api/test")
public class TestController implements TestControllerDocs {

    private final OAuthService oAuthService;

    @PostMapping("/auth/token/{userId}")
    public ApiResponse<String> generateDevToken(Long userId) {

        return ApiResponse.success(
                GeneralSuccessCode.OK, oAuthService.getUserAndAccessToken(userId));
    }
}
