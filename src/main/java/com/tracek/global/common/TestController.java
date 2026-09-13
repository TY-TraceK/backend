package com.tracek.global.common;

import com.tracek.domain.auth.application.service.OAuthService;
import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.application.dto.result.RelatedArtistRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedContentRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedLocationRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedMultiRankingResult;
import com.tracek.domain.ranking.application.service.VisitRankingProjectionService;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Profile({"dev", "local"})
@RequestMapping("/api/test")
public class TestController implements TestControllerDocs {

    private final OAuthService oAuthService;
    private final VisitRankingQueryService visitRankingQueryService;
    private final VisitRankingProjectionService visitRankingProjectionService;

    @PostMapping("/auth/token/{userId}")
    public ApiResponse<String> generateDevToken(@PathVariable Long userId) {
        return ApiResponse.success(
                GeneralSuccessCode.OK, oAuthService.getUserAndAccessToken(userId));
    }

    @GetMapping("/auth/success")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "jwtAuth")
    public String getTestAuthWithSuccess() {
        return "인증 성공";
    }

    @PostMapping("/ranking/projection/increase")
    public ApiResponse<Void> increaseRankingProjection(
            @RequestParam Long locationId,
            @RequestParam(required = false) Long contentId,
            @RequestParam(required = false) Long artistId) {
        visitRankingProjectionService.increase(locationId, contentId, artistId);

        return ApiResponse.success(GeneralSuccessCode.OK, null);
    }

    @PostMapping("/ranking/projection/decrease")
    public ApiResponse<Void> decreaseRankingProjection(
            @RequestParam Long locationId,
            @RequestParam(required = false) Long contentId,
            @RequestParam(required = false) Long artistId) {
        visitRankingProjectionService.decrease(locationId, contentId, artistId);

        return ApiResponse.success(GeneralSuccessCode.OK, null);
    }

    @GetMapping("/ranking/content/{contentId}/artists")
    public RankingSliceResult<RelatedArtistRankingResult> getArtistsByContent(
            @PathVariable Long contentId,
            @RequestParam(required = false) Long lastCount,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") Integer size) {
        RankingCondition condition = new RankingCondition(lastCount, lastId, size);

        return visitRankingQueryService.getArtistsByContent(contentId, condition);
    }

    @GetMapping("/ranking/content/{contentId}/locations")
    public RankingSliceResult<RelatedLocationRankingResult> getLocationsByContent(
            @PathVariable Long contentId,
            @RequestParam(required = false) Long lastCount,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") Integer size) {
        RankingCondition condition = new RankingCondition(lastCount, lastId, size);

        return visitRankingQueryService.getLocationsByContent(contentId, condition);
    }

    @GetMapping("/ranking/content/{contentId}/multi")
    public RankingSliceResult<RelatedMultiRankingResult> getMultiRankingByContent(
            @PathVariable Long contentId) {
        return visitRankingQueryService.getMultiRankingByContent(contentId);
    }

    /*
     * Artist 기준
     */

    @GetMapping("/ranking/artist/{artistId}/locations")
    public RankingSliceResult<RelatedLocationRankingResult> getLocationsByArtist(
            @PathVariable Long artistId,
            @RequestParam(required = false) Long lastCount,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") Integer size) {
        RankingCondition condition = new RankingCondition(lastCount, lastId, size);

        return visitRankingQueryService.getLocationsByArtist(artistId, condition);
    }

    @GetMapping("/ranking/artist/{artistId}/contents")
    public RankingSliceResult<RelatedContentRankingResult> getContentsByArtist(
            @PathVariable Long artistId,
            @RequestParam(required = false) Long lastCount,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") Integer size) {
        RankingCondition condition = new RankingCondition(lastCount, lastId, size);

        return visitRankingQueryService.getContentsByArtist(artistId, condition);
    }

    @GetMapping("/ranking/artist/{artistId}/multi")
    public RankingSliceResult<RelatedMultiRankingResult> getMultiRankingByArtist(
            @PathVariable Long artistId) {
        return visitRankingQueryService.getMultiRankingByArtist(artistId);
    }

    /*
     * Location 기준
     */

    @GetMapping("/ranking/location/{locationId}/artists")
    public RankingSliceResult<RelatedArtistRankingResult> getArtistsByLocation(
            @PathVariable Long locationId,
            @RequestParam(required = false) Long lastCount,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") Integer size) {
        RankingCondition condition = new RankingCondition(lastCount, lastId, size);

        return visitRankingQueryService.getArtistsByLocation(locationId, condition);
    }

    @GetMapping("/ranking/location/{locationId}/contents")
    public RankingSliceResult<RelatedContentRankingResult> getContentsByLocation(
            @PathVariable Long locationId,
            @RequestParam(required = false) Long lastCount,
            @RequestParam(required = false) Long lastId,
            @RequestParam(defaultValue = "10") Integer size) {
        RankingCondition condition = new RankingCondition(lastCount, lastId, size);

        return visitRankingQueryService.getContentsByLocation(locationId, condition);
    }

    @GetMapping("/ranking/location/{locationId}/multi")
    public RankingSliceResult<RelatedMultiRankingResult> getMultiRankingByLocation(
            @PathVariable Long locationId) {
        return visitRankingQueryService.getMultiRankingByLocation(locationId);
    }
}
