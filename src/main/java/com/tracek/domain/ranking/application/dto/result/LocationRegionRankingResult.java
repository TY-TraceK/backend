package com.tracek.domain.ranking.application.dto.result;

import com.tracek.domain.ranking.domain.model.LocationRankingView;
import lombok.Builder;

@Builder
public record LocationRegionRankingResult(int rank, String region, long totalVerificationCount) {

    public static LocationRegionRankingResult from(LocationRankingView view) {
        return LocationRegionRankingResult.builder()
                .rank(view.rank())
                .region(view.cityName())
                .totalVerificationCount(view.totalVerificationCount())
                .build();
    }
}
