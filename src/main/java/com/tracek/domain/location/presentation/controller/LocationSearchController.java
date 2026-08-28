package com.tracek.domain.location.presentation.controller;

import com.tracek.domain.location.application.dto.LocationSearchQuery;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import com.tracek.domain.location.application.service.LocationSearchQueryService;
import com.tracek.domain.location.presentation.request.LocationSearchRequest;
import com.tracek.domain.location.presentation.response.LocationSearchResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
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
            summary = "관광지 검색 (커서 기반)",
            description =
                    "이름·시/도·구군을 대상으로 전문 검색(FULLTEXT, ngram)을 수행합니다. "
                            + "keyword가 없으면 빈 목록을 반환합니다. category를 지정하면 해당 카테고리로 추가 필터링합니다. "
                            + "lastLocationId를 응답의 lastId로 채워 다음 페이지를 커서 기반으로 조회합니다.")
    @GetMapping("/search")
    public ApiResponse<LocationSearchResponse> searchLocations(
            @ParameterObject @ModelAttribute LocationSearchRequest request) {
        LocationSearchQuery query = request.toQuery();
        LocationSearchResult result = locationSearchQueryService.searchLocations(query);
        return ApiResponse.success(GeneralSuccessCode.OK, LocationSearchResponse.from(result));
    }
}
