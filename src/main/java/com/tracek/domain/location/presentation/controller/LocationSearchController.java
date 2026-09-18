package com.tracek.domain.location.presentation.controller;

import com.tracek.domain.location.application.dto.LocationBoundsQuery;
import com.tracek.domain.location.application.dto.LocationBoundsResult;
import com.tracek.domain.location.application.dto.LocationSearchQuery;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import com.tracek.domain.location.application.service.LocationSearchQueryService;
import com.tracek.domain.location.presentation.request.LocationBoundsRequest;
import com.tracek.domain.location.presentation.request.LocationSearchRequest;
import com.tracek.domain.location.presentation.response.LocationBoundsResponse;
import com.tracek.domain.location.presentation.response.LocationSearchResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Location", description = "관광지 조회 API")
@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationSearchController {
    private final LocationSearchQueryService locationSearchQueryService;

    @Operation(
            summary = "관광지 이름 검색 (통합검색용, 커서 기반)",
            description =
                    "관광지 이름만 대상으로 전문 검색(FULLTEXT, ngram)을 수행합니다. "
                            + "keyword가 없으면 빈 목록을 반환합니다. "
                            + "lastLocationId를 응답의 lastId로 채워 다음 페이지를 커서 기반으로 조회합니다.")
    @GetMapping("/search")
    public ApiResponse<LocationSearchResponse> searchLocationsByName(
            @ParameterObject @ModelAttribute LocationSearchRequest request) {
        LocationSearchQuery query = request.toQuery();
        LocationSearchResult result = locationSearchQueryService.searchLocationsByName(query);
        return ApiResponse.success(GeneralSuccessCode.OK, LocationSearchResponse.from(result));
    }

    @Operation(
            summary = "관광지 검색 - 지역명 포함 (커서 기반)",
            description =
                    "이름·시/도·구군을 대상으로 전문 검색(FULLTEXT, ngram)을 수행합니다. "
                            + "keyword가 없으면 빈 목록을 반환합니다. "
                            + "lastLocationId를 응답의 lastId로 채워 다음 페이지를 커서 기반으로 조회합니다.")
    @GetMapping("/search-region")
    public ApiResponse<LocationSearchResponse> searchLocations(
            @ParameterObject @ModelAttribute LocationSearchRequest request) {
        LocationSearchQuery query = request.toQuery();
        LocationSearchResult result = locationSearchQueryService.searchLocations(query);
        return ApiResponse.success(GeneralSuccessCode.OK, LocationSearchResponse.from(result));
    }

    @Operation(
            summary = "지도 bounds 범위 내 관광지 조회 (카테고리/북마크 필터 가능)",
            description =
                    "지도 화면에 보이는 사각형 범위(남서/북동 좌표) 안의 관광지를 페이징 없이 조회합니다. "
                            + "4개 좌표를 모두 입력하지 않으면 부산광역시청 기준 기본 범위로 조회합니다. "
                            + "위경도 차이가 0.3도를 넘으면 범위가 너무 넓다는 에러를 반환하니 화면을 확대한 뒤 다시 요청해주세요. "
                            + "category를 지정하면 해당 카테고리로 추가 필터링합니다(선택, 예: ATTRACTION, CAFE). "
                            + "archivedOnly=true면 로그인 유저가 북마크한 관광지만 필터링하며, 비로그인 상태면 빈 목록을 반환합니다.")
    @GetMapping("/bounds")
    public ApiResponse<LocationBoundsResponse> findLocationsWithinBounds(
            @ParameterObject @ModelAttribute LocationBoundsRequest request,
            @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal) {
        LocationBoundsQuery query = request.toQuery();
        Long userId = principal == null ? null : principal.userId();
        LocationBoundsResult result =
                locationSearchQueryService.findLocationsWithinBounds(query, userId);
        return ApiResponse.success(GeneralSuccessCode.OK, LocationBoundsResponse.from(result));
    }
}
