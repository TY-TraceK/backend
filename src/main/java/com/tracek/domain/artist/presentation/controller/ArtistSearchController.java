package com.tracek.domain.artist.presentation.controller;

import com.tracek.domain.artist.application.dto.ArtistSearchQuery;
import com.tracek.domain.artist.application.dto.ArtistSearchResult;
import com.tracek.domain.artist.application.service.ArtistSearchQueryService;
import com.tracek.domain.artist.presentation.request.ArtistSearchRequest;
import com.tracek.domain.artist.presentation.response.ArtistSearchResponse;
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

@Tag(name = "Artist", description = "아티스트 조회 API")
@RestController
@RequestMapping("/api/artists")
@RequiredArgsConstructor
public class ArtistSearchController {

    private final ArtistSearchQueryService artistSearchQueryService;

    @Operation(
            summary = "아티스트 검색 (커서 기반)",
            description =
                    "이름·alias를 대상으로 전문 검색(FULLTEXT, ngram)을 수행합니다. "
                            + "lastArtistId를 응답의 lastId로 채워 다음 페이지를 커서 기반으로 조회합니다.")
    @GetMapping("/search")
    public ApiResponse<ArtistSearchResponse> searchArtists(
            @ParameterObject @ModelAttribute ArtistSearchRequest request) {
        ArtistSearchQuery query = request.toQuery();
        ArtistSearchResult result = artistSearchQueryService.searchArtists(query);
        return ApiResponse.success(GeneralSuccessCode.OK, ArtistSearchResponse.from(result));
    }
}
