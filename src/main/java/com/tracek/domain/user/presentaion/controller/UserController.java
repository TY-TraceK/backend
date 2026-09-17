package com.tracek.domain.user.presentaion.controller;

import com.tracek.domain.user.application.service.UserCommandService;
import com.tracek.domain.user.application.service.UserQueryService;
import com.tracek.domain.user.presentaion.controller.docs.UserControllerDocs;
import com.tracek.domain.user.presentaion.dto.request.UserProfileUpdateRequest;
import com.tracek.domain.user.presentaion.dto.response.UserProfileResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserControllerDocs {

    private final UserCommandService userCommandService;
    private final UserQueryService userQueryService;

    @PostMapping("/me")
    @Override
    public ApiResponse<UserProfileResponse> updateUserProfile(
            AuthenticationPrincipal authenticationPrincipal, UserProfileUpdateRequest request) {
        return ApiResponse.success(
                GeneralSuccessCode.OK,
                UserProfileResponse.from(
                        userCommandService.updateUserProfile(
                                request.toCommand(authenticationPrincipal.userId()))));
    }

    @Override
    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> getUserProfile(
            AuthenticationPrincipal authenticationPrincipal) {
        return ApiResponse.success(
                GeneralSuccessCode.OK,
                UserProfileResponse.from(
                        userQueryService.getUserProfileData(authenticationPrincipal.userId())));
    }
}
