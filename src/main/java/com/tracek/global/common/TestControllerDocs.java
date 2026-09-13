package com.tracek.global.common;

import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.application.dto.result.RelatedArtistRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedContentRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedLocationRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedMultiRankingResult;
import com.tracek.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "TEST", description = "테스트용 API")
public interface TestControllerDocs {

    /*
     * AUTH
     */

    @Tag(name = "AUTH", description = "인증/인가")
    @Operation(
            summary = "개발용 테스트 토큰 발급",
            description = "유저 ID를 직접 입력하여 JWT Access Token을 즉시 발급받습니다.")
    ApiResponse<String> generateDevToken(
            @Parameter(description = "유저 아이디", required = true) @PathVariable Long userId);

    @Tag(name = "AUTH", description = "인증/인가")
    @Operation(summary = "인증 테스트", description = "현재 요청의 인증 여부를 확인합니다.")
    String getTestAuthWithSuccess();

    /*
     * RANKING - Projection
     */

    @Tag(name = "RANKING", description = "랭킹")
    @Operation(
            summary = "랭킹 Projection 증가 테스트",
            description = "방문 인증 발생 시 실행되는 랭킹 증가 로직을 직접 호출합니다.")
    ApiResponse<Void> increaseRankingProjection(
            @Parameter(description = "관광지 ID", required = true) @RequestParam Long locationId,
            @Parameter(description = "콘텐츠 ID") @RequestParam(required = false) Long contentId,
            @Parameter(description = "아티스트 ID") @RequestParam(required = false) Long artistId);

    @Tag(name = "RANKING", description = "랭킹")
    @Operation(
            summary = "랭킹 Projection 감소 테스트",
            description = "방문 인증 취소 시 실행되는 랭킹 감소 로직을 직접 호출합니다.")
    ApiResponse<Void> decreaseRankingProjection(
            @Parameter(description = "관광지 ID", required = true) @RequestParam Long locationId,
            @Parameter(description = "콘텐츠 ID") @RequestParam(required = false) Long contentId,
            @Parameter(description = "아티스트 ID") @RequestParam(required = false) Long artistId);

    /*
     * RANKING - Content
     */

    @Tag(name = "RANKING", description = "랭킹")
    @Operation(summary = "콘텐츠 기준 아티스트 랭킹 조회")
    RankingSliceResult<RelatedArtistRankingResult> getArtistsByContent(
            @PathVariable Long contentId,
            @RequestParam(required = false) Long lastCount,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") Integer size);

    @Tag(name = "RANKING", description = "랭킹")
    @Operation(summary = "콘텐츠 기준 관광지 랭킹 조회")
    RankingSliceResult<RelatedLocationRankingResult> getLocationsByContent(
            @PathVariable Long contentId,
            @RequestParam(required = false) Long lastCount,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") Integer size);

    @Tag(name = "RANKING", description = "랭킹")
    @Operation(summary = "콘텐츠 기준 복합 랭킹 조회")
    RankingSliceResult<RelatedMultiRankingResult> getMultiRankingByContent(
            @PathVariable Long contentId);

    /*
     * RANKING - Artist
     */

    @Tag(name = "RANKING", description = "랭킹")
    @Operation(summary = "아티스트 기준 관광지 랭킹 조회")
    RankingSliceResult<RelatedLocationRankingResult> getLocationsByArtist(
            @PathVariable Long artistId,
            @RequestParam(required = false) Long lastCount,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") Integer size);

    @Tag(name = "RANKING", description = "랭킹")
    @Operation(summary = "아티스트 기준 콘텐츠 랭킹 조회")
    RankingSliceResult<RelatedContentRankingResult> getContentsByArtist(
            @PathVariable Long artistId,
            @RequestParam(required = false) Long lastCount,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") Integer size);

    @Tag(name = "RANKING", description = "랭킹")
    @Operation(summary = "아티스트 기준 복합 랭킹 조회")
    RankingSliceResult<RelatedMultiRankingResult> getMultiRankingByArtist(
            @PathVariable Long artistId);

    /*
     * RANKING - Location
     */

    @Tag(name = "RANKING", description = "랭킹")
    @Operation(summary = "관광지 기준 아티스트 랭킹 조회")
    RankingSliceResult<RelatedArtistRankingResult> getArtistsByLocation(
            @PathVariable Long locationId,
            @RequestParam(required = false) Long lastCount,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") Integer size);

    @Tag(name = "RANKING", description = "랭킹")
    @Operation(summary = "관광지 기준 콘텐츠 랭킹 조회")
    RankingSliceResult<RelatedContentRankingResult> getContentsByLocation(
            @PathVariable Long locationId,
            @RequestParam(required = false) Long lastCount,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") Integer size);

    @Tag(name = "RANKING", description = "랭킹")
    @Operation(summary = "관광지 기준 복합 랭킹 조회")
    RankingSliceResult<RelatedMultiRankingResult> getMultiRankingByLocation(
            @PathVariable Long locationId);
}
