package com.tracek.domain.ranking.presentaion.controller;

import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import com.tracek.domain.ranking.presentaion.controller.docs.RankingControllerDocs;
import com.tracek.domain.ranking.presentaion.dto.request.RegionRankingRequest;
import com.tracek.domain.ranking.presentaion.dto.response.LocationRegionRankingResponse;
import com.tracek.domain.ranking.presentaion.dto.response.RankingTopResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController implements RankingControllerDocs {

    private final VisitRankingQueryService visitRankingQueryService;

    @Override
    @GetMapping("/region/top")
    public ApiResponse<RankingTopResponse<LocationRegionRankingResponse>> getRegionRanking(
            RegionRankingRequest request) {
        return ApiResponse.success(
                GeneralSuccessCode.OK,
                RankingTopResponse.from(
                        visitRankingQueryService.getRegionRanking(request.toCondition()),
                        LocationRegionRankingResponse::from));
    }
}
