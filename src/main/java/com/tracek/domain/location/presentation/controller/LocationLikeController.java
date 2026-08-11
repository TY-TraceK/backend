package com.tracek.domain.location.presentation.controller;

import com.tracek.domain.location.application.service.LocationLikeCommandService;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locations/{locationId}/likes")
@RequiredArgsConstructor
public class LocationLikeController {
    private final LocationLikeCommandService locationLikeCommandService;

    // 좋아요 등록 (PUT-멱등)
    @PutMapping
    public ApiResponse<Void> like(
            @PathVariable Long locationId,
            @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal) {
        locationLikeCommandService.like(principal.userId(), locationId);
        return ApiResponse.success(GeneralSuccessCode.OK);
    }

    // 좋아요 취소 (DELETE-멱등)
    @DeleteMapping
    public ApiResponse<Void> unlike(
            @PathVariable Long locationId,
            @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal) {
        locationLikeCommandService.unlike(principal.userId(), locationId);
        return ApiResponse.success(GeneralSuccessCode.OK);
    }
}
