package com.tracek.domain.auth.presentation.controller.docs;

import com.tracek.domain.auth.presentation.dto.response.OAuthLoginResponse;
import com.tracek.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "AUTH", description = "인증/인가")
public interface AuthControllerDocs {

    @Operation(summary = "oauth 회원가입", description = "oauth provider에 따라 회원가입 또는 로그인을 제공합니다. ")
    ApiResponse<OAuthLoginResponse> createOauthLogin(
            @Parameter(description = "Oauth Code", required = true)
                    @RequestParam
                    @NotBlank(message = "Oauth에서 발급된 코드를 입력해주세요.")
                    String code,
            @Parameter(description = "Oauth 종류", required = true) @PathVariable String provider);
}
