package com.tracek.domain.content.presentation.controller;

import com.tracek.domain.content.application.dto.ContentDetailResult;
import com.tracek.domain.content.application.facade.ContentFacade;
import com.tracek.domain.content.presentation.response.ContentDetailResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Content", description = "콘텐츠 조회 API")
@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentQueryController {
    private final ContentFacade contentFacade;

    @Operation(
            summary = "콘텐츠 단건 상세 조회",
            description = "콘텐츠 ID로 상세 정보와 연관 관광지를 조회합니다. 관광지별로 출연 아티스트가 중첩된 계층형 구조로 응답합니다.")
    @GetMapping("/{contentId}")
    public ApiResponse<ContentDetailResponse> getContentDetails(
            @Parameter(description = "콘텐츠 ID") @PathVariable Long contentId
    ) {
        ContentDetailResult result = contentFacade.getContentDetails(contentId);
        return ApiResponse.success(GeneralSuccessCode.OK, ContentDetailResponse.from(result));
    }
}
