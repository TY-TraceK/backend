package com.tracek.domain.location.presentation.controller;

import com.tracek.domain.location.application.service.LocationArchiveCommandService;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Location-Archive", description = "관광지 아카이브 API")
@RestController
@RequestMapping("/api/locations/{locationId}/archives")
@RequiredArgsConstructor
public class LocationArchiveController {
    private final LocationArchiveCommandService locationArchiveCommandService;

    @Operation(
            summary = "관광지 아카이브 등록",
            description =
                    "로그인한 유저가 관광지를 아카이브(스크랩)합니다. 이미 아카이브한 상태면 아무 동작 없이 성공 응답만 반환합니다(순차 재요청 기준 멱등, 동시 요청 시엔 데이터 정합성만 보장). 인증 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "jwtAuth"))
    @PutMapping
    public ApiResponse<Void> archive(
            @Parameter(description = "관광지 ID") @PathVariable Long locationId,
            @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal) {
        locationArchiveCommandService.archive(principal.userId(), locationId);
        return ApiResponse.success(GeneralSuccessCode.OK);
    }

    @Operation(
            summary = "관광지 아카이브 취소",
            description =
                    "로그인한 유저가 관광지 아카이브를 취소합니다. 아카이브하지 않은 상태면 아무 동작 없이 성공 응답만 반환합니다(순차 재요청 기준 멱등, 동시 요청 시엔 카운트 정합성이 깨질 수 있는 알려진 한계 있음). 인증 토큰이 필요합니다.",
            security = @SecurityRequirement(name = "jwtAuth"))
    @DeleteMapping
    public ApiResponse<Void> deleteArchive(
            @Parameter(description = "관광지 ID") @PathVariable Long locationId,
            @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal) {
        locationArchiveCommandService.deleteArchive(principal.userId(), locationId);
        return ApiResponse.success(GeneralSuccessCode.OK);
    }
}
