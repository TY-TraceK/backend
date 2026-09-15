package com.tracek.domain.ranking.application.dto.result;

import com.tracek.domain.ranking.domain.model.LocationRankingView;
import lombok.Builder;

@Builder
public record LocationRankingResult(
        Integer rank,
        Long locationId,
        String locationName,
        String cityName,
        Long totalVerificationCount) {

    public static LocationRankingResult from(LocationRankingView rankingView) {
        return LocationRankingResult.builder()
                .rank(rankingView.rank())
                .locationId(rankingView.locationId())
                .locationName(rankingView.locationName())
                .cityName(rankingView.cityName())
                .totalVerificationCount(rankingView.totalVerificationCount())
                .build();
    }
}
