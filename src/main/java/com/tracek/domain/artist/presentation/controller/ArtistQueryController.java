package com.tracek.domain.artist.presentation.controller;

import com.tracek.domain.artist.application.dto.ArtistDetailResult;
import com.tracek.domain.artist.application.dto.ArtistSummaryResult;
import com.tracek.domain.artist.application.facade.ArtistFacade;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.presentation.response.ArtistDetailResponse;
import com.tracek.domain.artist.presentation.response.ArtistSummaryResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Artist", description = "아티스트 조회 API")
@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistQueryController {
    private final ArtistFacade artistFacade;
    private final ArtistQueryService artistQueryService;

    @Operation(
            summary = "아티스트 단건 상세 조회",
            description = "아티스트 ID로 상세 정보와 연관 콘텐츠를 조회합니다. 콘텐츠별로 촬영 관광지가 중첩된 계층형 구조로 응답합니다.")
    @GetMapping("/{artistId}")
    public ApiResponse<ArtistDetailResponse> getArtistDetails(
            @Parameter(description = "아티스트 ID") @PathVariable Long artistId) {
        ArtistDetailResult result = artistFacade.getArtistDetails(artistId);
        return ApiResponse.success(GeneralSuccessCode.OK, ArtistDetailResponse.from(result));
    }

    @Operation(summary = "아티스트 전체 목록 조회", description = "아티스트 목록을 페이징 조회합니다.")
    @GetMapping
    public ApiResponse<Page<ArtistSummaryResponse>> getArtists(
            @ParameterObject
                    @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC)
                    Pageable pageable) {
        Page<ArtistSummaryResult> artists = artistQueryService.getAllArtists(pageable);
        Page<ArtistSummaryResponse> artistResponses = artists.map(ArtistSummaryResponse::from);
        return ApiResponse.success(GeneralSuccessCode.OK, artistResponses);
    }
}
