package com.tracek.domain.ranking.presentaion.dto.response;

import com.tracek.domain.ranking.application.dto.result.LocationRegionRankingResult;

public record LocationRegionRankingResponse(int rank, String region, long totalVerificationCount) {

    public static LocationRegionRankingResponse from(LocationRegionRankingResult result) {
        return new LocationRegionRankingResponse(
                result.rank(), result.region(), result.totalVerificationCount());
    }
}
