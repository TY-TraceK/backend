package com.tracek.domain.location.presentation.controller;

import com.tracek.domain.location.application.dto.LocationSimpleResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.presentation.response.LocationSimpleResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Location-MyPage", description = "마이페이지 관광지 조회 API")
@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class LocationMyPageController {
    private final LocationQueryService locationQueryService;

    @Operation(
            summary = "마이페이지 - 내가 좋아요한 관광지 목록 조회",
            description = "로그인한 유저가 좋아요한 관광지 목록을 최신순으로 조회합니다. 인증 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "jwtAuth"))
    @GetMapping("/liked-locations")
    public ApiResponse<List<LocationSimpleResponse>> getLikedLocations(
            @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal) {
        List<LocationSimpleResult> locations =
                locationQueryService.getLikedLocations(principal.userId());
        List<LocationSimpleResponse> response =
                locations.stream().map(LocationSimpleResponse::from).toList();
        return ApiResponse.success(GeneralSuccessCode.OK, response);
    }

    @Operation(
            summary = "마이페이지 - 내가 북마크한 관광지 목록 조회",
            description = "로그인한 유저가 북마크(아카이브)한 관광지 목록을 최신순으로 조회합니다. 인증 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "jwtAuth"))
    @GetMapping("/archived-locations")
    public ApiResponse<List<LocationSimpleResponse>> getArchivedLocations(
            @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal) {
        List<LocationSimpleResult> locations =
                locationQueryService.getArchivedLocations(principal.userId());
        List<LocationSimpleResponse> response =
                locations.stream().map(LocationSimpleResponse::from).toList();
        return ApiResponse.success(GeneralSuccessCode.OK, response);
    }
}
