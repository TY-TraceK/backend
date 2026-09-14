package com.tracek.domain.search.presentation.controller;

import com.tracek.domain.search.application.dto.UnifiedSearchResult;
import com.tracek.domain.search.application.facade.SearchFacade;
import com.tracek.domain.search.presentation.response.UnifiedSearchResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Search", description = "통합검색 API")
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchFacade searchFacade;

    @Operation(
            summary = "통합검색 (아티스트/콘텐츠/관광지)",
            description =
                    "keyword를 아티스트 이름, 콘텐츠 제목, 관광지 이름 각각에 대해 검색해 섹션별로 묶어 반환합니다. "
                            + "페이징 없이 도메인별 상위 20개까지만 반환합니다.")
    @GetMapping
    public ApiResponse<UnifiedSearchResponse> search(@RequestParam String keyword) {
        UnifiedSearchResult result = searchFacade.search(keyword);
        return ApiResponse.success(GeneralSuccessCode.OK, UnifiedSearchResponse.from(result));
    }
}
