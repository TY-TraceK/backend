package com.tracek.domain.user.presentaion.controller.docs;

import com.tracek.domain.user.presentaion.controller.UserActivityProjectionResponse;
import com.tracek.domain.user.presentaion.dto.request.UserProfileUpdateRequest;
import com.tracek.domain.user.presentaion.dto.response.UserProfileResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Tag(name = "USER", description = "유저 관련")
public interface UserControllerDocs {

    @SecurityRequirement(name = "jwtAuth")
    @Operation(summary = "유저 프로필 정보 수정", description = "유저 프로필 정보를 업데이트 합니다")
    ApiResponse<UserProfileResponse> updateUserProfile(
            @org.springframework.security.core.annotation.AuthenticationPrincipal
                    AuthenticationPrincipal authenticationPrincipal,
            @Valid @ModelAttribute UserProfileUpdateRequest request);

    @SecurityRequirement(name = "jwtAuth")
    @Operation(summary = "유저 프로필 정보 조회", description = "유저 프로필 정보를 조회합니다.")
    ApiResponse<UserProfileResponse> getUserProfile(
            @org.springframework.security.core.annotation.AuthenticationPrincipal
                    AuthenticationPrincipal authenticationPrincipal);

    @SecurityRequirement(name = "jwtAuth")
    @GetMapping("/me/projection")
    ApiResponse<UserActivityProjectionResponse> getUserActivityProjection(
            @org.springframework.security.core.annotation.AuthenticationPrincipal
                    AuthenticationPrincipal authenticationPrincipal);
}
