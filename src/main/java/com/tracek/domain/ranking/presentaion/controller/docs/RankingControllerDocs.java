package com.tracek.domain.ranking.presentaion.controller.docs;

import com.tracek.domain.ranking.presentaion.dto.request.LocationRankingRequest;
import com.tracek.domain.ranking.presentaion.dto.request.RegionRankingRequest;
import com.tracek.domain.ranking.presentaion.dto.response.LocationRankingResponse;
import com.tracek.domain.ranking.presentaion.dto.response.LocationRegionRankingResponse;
import com.tracek.domain.ranking.presentaion.dto.response.RankingTopResponse;
import com.tracek.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;

@Tag(name = "RANKING", description = "랭킹")
public interface RankingControllerDocs {

    @Operation(summary = "지역별 랭킹 조회", description = "지역별 랭킹 조회")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "랭킹 조회 성공",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            })
    ApiResponse<RankingTopResponse<LocationRegionRankingResponse>> getRegionRanking(
            @Valid @ParameterObject RegionRankingRequest request);

    @Operation(summary = "관광지 랭킹 조회", description = "관광지 랭킹 조회(장소, 카테고리)")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "랭킹 조회 성공",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            })
    ApiResponse<RankingTopResponse<LocationRankingResponse>> getLocationRanking(
            @Valid @ParameterObject LocationRankingRequest request);
}
