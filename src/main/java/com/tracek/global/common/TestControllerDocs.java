package com.tracek.global.common;

import com.tracek.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "TEST", description = "테스트용 API")
public interface TestControllerDocs {

    @Tag(name = "AUTH", description = "인증/인가")
    @Operation(
            summary = "개발용 테스트 토큰 발급",
            description = "유저 ID를 직접 입력하여 JWT Access Token을 즉시 발급받습니다.")
    ApiResponse<String> generateDevToken(
            @Parameter(description = "유저 아이디", required = true) @PathVariable Long userId);
}
