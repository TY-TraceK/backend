package com.tracek.domain.location.presentation.controller;

import com.tracek.domain.location.application.dto.*;
import com.tracek.domain.location.application.facade.LocationFacade;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.presentation.request.LocationNearbyRequest;
import com.tracek.domain.location.presentation.response.*;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Location", description = "관광지 조회 API")
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationQueryController {
    private final LocationFacade locationFacade;
    private final LocationQueryService locationQueryService;

    @Operation(
            summary = "관광지 단건 상세 조회",
            description = "관광지 ID로 상세 정보와 연관 콘텐츠를 조회합니다. 콘텐츠별로 출연 아티스트가 중첩된 계층형 구조로 응답합니다.")
    @GetMapping("/{locationId}")
    public ApiResponse<LocationDetailResponse> getLocationDetails(
            @Parameter(description = "관광지 ID") @PathVariable Long locationId) {
        LocationDetailResult result = locationFacade.getLocationDetails(locationId);
        return ApiResponse.success(GeneralSuccessCode.OK, LocationDetailResponse.from(result));
    }

    @Operation(
            summary = "지도 범위 내 관광지 리스트 조회",
            description = "현재 위치(위도/경도)와 반경을 기준으로 주변 관광지 목록을 거리순으로 조회합니다.")
    @GetMapping("/map")
    public ApiResponse<List<LocationNearbyResponse>> getNearbyLocations(
            @Valid @ParameterObject @ModelAttribute LocationNearbyRequest request) {
        List<LocationNearbyResult> results =
                locationQueryService.getNearbyLocations(
                        request.getLat(), request.getLng(), request.getRadiusMeter());

        List<LocationNearbyResponse> response =
                results.stream().map(LocationNearbyResponse::from).collect(Collectors.toList());

        return ApiResponse.success(GeneralSuccessCode.OK, response);
    }

    @Operation(
            summary = "관광지 연관 콘텐츠·아티스트 조회",
            description = "해당 관광지 정보와 연관된 콘텐츠 및 아티스트 정보를 매핑하여 조회합니다.")
    @GetMapping("/{locationId}/related-info")
    public ApiResponse<LocationRelatedInfoResponse> getRelatedContentAndArtists(
            @Parameter(description = "관광지 ID") @PathVariable Long locationId) {

        LocationRelatedInfoResult result = locationFacade.getRelatedContentAndArtists(locationId);
        return ApiResponse.success(GeneralSuccessCode.OK, LocationRelatedInfoResponse.from(result));
    }

    @Operation(summary = "관광지 전체 목록 조회", description = "관광지 목록을 페이징 조회합니다.")
    @GetMapping
    public ApiResponse<Page<LocationSummaryResponse>> getLocations(
            @ParameterObject
                    @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        Page<LocationSummaryResult> locations = locationQueryService.getAllLocations(pageable);
        Page<LocationSummaryResponse> locationResponses =
                locations.map(LocationSummaryResponse::from);
        return ApiResponse.success(GeneralSuccessCode.OK, locationResponses);
    }

    @Operation(summary = "관광지 카테고리별 목록 조회", description = "카테고리로 관광지 목록을 페이징 조회합니다.")
    @GetMapping("/category/{category}")
    public ApiResponse<Page<LocationSummaryResponse>> getLocationsByCategory(
            @Parameter(description = "관광지 카테고리 (예: ATTRACTION, CAFE)") @PathVariable
                    String category,
            @ParameterObject
                    @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        Page<LocationSummaryResult> locations =
                locationQueryService.getLocationsByCategory(category, pageable);
        Page<LocationSummaryResponse> locationResponses =
                locations.map(LocationSummaryResponse::from);
        return ApiResponse.success(GeneralSuccessCode.OK, locationResponses);
    }
}
