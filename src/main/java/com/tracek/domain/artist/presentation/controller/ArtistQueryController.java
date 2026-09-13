package com.tracek.domain.artist.presentation.controller;

import com.tracek.domain.artist.application.dto.ArtistDetailRelatedContentResult;
import com.tracek.domain.artist.application.dto.ArtistDetailRelatedLocationResult;
import com.tracek.domain.artist.application.dto.ArtistDetailResult;
import com.tracek.domain.artist.application.dto.ArtistSummaryResult;
import com.tracek.domain.artist.application.facade.ArtistFacade;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.presentation.request.ArtistDetailRelatedContentRequest;
import com.tracek.domain.artist.presentation.request.ArtistDetailRelatedLocationRequest;
import com.tracek.domain.artist.presentation.response.ArtistDetailRelatedContentResponse;
import com.tracek.domain.artist.presentation.response.ArtistDetailRelatedLocationResponse;
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
import org.springframework.web.bind.annotation.*;

@Tag(name = "Artist", description = "아티스트 조회 API")
@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistQueryController {
    private final ArtistFacade artistFacade;
    private final ArtistQueryService artistQueryService;

    @Operation(
            summary = "아티스트 단건 상세 조회",
            description = "아티스트 ID로 상세 정보와 연관 관광지 및 콘텐츠를 조회합니다. 콘텐츠별로 촬영 관광지가 중첩된 계층형 구조로 응답합니다.")
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

    @Operation(
            summary = "아티스트 단건 상세 조회 - 관광지 탭",
            description = "아티스트 ID로 상세 정보와 연관 관광지를 방문 인증 랭킹 순으로 조회합니다. city로 필터링할 수 있습니다.")
    @GetMapping("/{artistId}/locations")
    public ApiResponse<ArtistDetailRelatedLocationResponse> getArtistDetailRelatedLocations(
            @Parameter(description = "아티스트 ID") @PathVariable Long artistId,
            @ParameterObject @ModelAttribute ArtistDetailRelatedLocationRequest request) {
        ArtistDetailRelatedLocationResult result =
                artistFacade.getArtistDetailsRelatedLocation(
                        artistId, request.getCity(), request.toCondition());
        return ApiResponse.success(
                GeneralSuccessCode.OK, ArtistDetailRelatedLocationResponse.from(result));
    }

    @Operation(
            summary = "아티스트 단건 상세 조회 - 콘텐츠 탭",
            description = "아티스트 ID로 상세 정보와 연관 콘텐츠 방문 인증 랭킹 순으로 조회합니다.")
    @GetMapping("/{artistId}/contents")
    public ApiResponse<ArtistDetailRelatedContentResponse> getArtistDetailRelatedContents(
            @Parameter(description = "아티스트 ID") @PathVariable Long artistId,
            @ParameterObject @ModelAttribute ArtistDetailRelatedContentRequest request) {
        ArtistDetailRelatedContentResult result =
                artistFacade.getArtistDetailsRelatedContent(artistId, request.toCondition());
        return ApiResponse.success(
                GeneralSuccessCode.OK, ArtistDetailRelatedContentResponse.from(result));
    }
}
