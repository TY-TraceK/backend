package com.tracek.domain.content.presentation.controller;

import com.tracek.domain.content.application.dto.ContentSearchQuery;
import com.tracek.domain.content.application.dto.ContentSearchResult;
import com.tracek.domain.content.application.service.ContentSearchQueryService;
import com.tracek.domain.content.presentation.request.ContentSearchRequest;
import com.tracek.domain.content.presentation.response.ContentSearchResponse;
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

@Tag(name = "Content", description = "콘텐츠 조회 API")
@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentSearchController {

    private final ContentSearchQueryService contentSearchQueryService;

    @Operation(
            summary = "콘텐츠 검색 (커서 기반)",
            description =
                    "제목을 대상으로 전문 검색(FULLTEXT, ngram)을 수행합니다. "
                            + "lastContentId를 응답의 lastId로 채워 다음 페이지를 커서 기반으로 조회합니다.")
    @GetMapping("/search")
    public ApiResponse<ContentSearchResponse> searchContents(
            @ParameterObject @ModelAttribute ContentSearchRequest request) {
        ContentSearchQuery query = request.toQuery();
        ContentSearchResult result = contentSearchQueryService.searchContents(query);
        return ApiResponse.success(GeneralSuccessCode.OK, ContentSearchResponse.from(result));
    }
}
