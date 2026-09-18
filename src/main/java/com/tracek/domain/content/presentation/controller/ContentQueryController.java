package com.tracek.domain.content.presentation.controller;

import com.tracek.domain.content.application.dto.ContentDetailResult;
import com.tracek.domain.content.application.dto.ContentSummaryResult;
import com.tracek.domain.content.application.facade.ContentFacade;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.presentation.request.ContentDetailRequest;
import com.tracek.domain.content.presentation.response.ContentDetailResponse;
import com.tracek.domain.content.presentation.response.ContentSummaryResponse;
import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Content", description = "콘텐츠 조회 API")
@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentQueryController {
    private final ContentFacade contentFacade;
    private final ContentQueryService contentQueryService;

    @Operation(
            summary = "콘텐츠 단건 상세 조회",
            description =
                    "콘텐츠 ID로 상세 정보(고정 출연진 포함)와 연관 관광지를 방문 인증 랭킹 순으로 조회합니다. city로 필터링할 수 있습니다.")
    @GetMapping("/{contentId}")
    public ApiResponse<ContentDetailResponse> getContentDetails(
            @Parameter(hidden = true) @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal,
            @Parameter(description = "콘텐츠 ID") @PathVariable Long contentId,
            @ParameterObject @ModelAttribute ContentDetailRequest request) {
        Long userId = principal == null ? null : principal.userId();
        RankingCondition condition = request.toCondition();
        ContentDetailResult result =
                contentFacade.getContentDetails(userId, contentId, request.getCity(), condition);
        return ApiResponse.success(GeneralSuccessCode.OK, ContentDetailResponse.from(result));
    }

    @Operation(summary = "콘텐츠 전체 목록 조회", description = "콘텐츠 목록을 페이징 조회합니다.")
    @GetMapping
    public ApiResponse<Page<ContentSummaryResponse>> getContents(
            @ParameterObject
                    @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        Page<ContentSummaryResult> contents = contentQueryService.getAllContents(pageable);
        Page<ContentSummaryResponse> contentResponses = contents.map(ContentSummaryResponse::from);
        return ApiResponse.success(GeneralSuccessCode.OK, contentResponses);
    }

    @Operation(summary = "콘텐츠 카테고리별 목록 조회", description = "카테고리로 콘텐츠 목록을 페이징 조회합니다.")
    @GetMapping("/category/{category}")
    public ApiResponse<Page<ContentSummaryResponse>> getContentsByCategory(
            @Parameter(description = "콘텐츠 카테고리 (예: KPOP, DRAMA)") @PathVariable String category,
            @ParameterObject
                    @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        Page<ContentSummaryResult> contents =
                contentQueryService.getContentsByCategory(category, pageable);
        Page<ContentSummaryResponse> contentResponses = contents.map(ContentSummaryResponse::from);
        return ApiResponse.success(GeneralSuccessCode.OK, contentResponses);
    }
}
