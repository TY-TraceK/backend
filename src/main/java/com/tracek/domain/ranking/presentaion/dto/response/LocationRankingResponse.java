package com.tracek.domain.ranking.presentaion.dto.response;

import com.tracek.domain.ranking.application.dto.result.LocationRankingResult;
import lombok.Builder;

@Builder
public record LocationRankingResponse(
        Integer rank,
        Long locationId,
        String locationName,
        String cityName,
        String imageUrl,
        Long totalVerificationCount) {

    public static LocationRankingResponse from(LocationRankingResult result) {
        return LocationRankingResponse.builder()
                .rank(result.rank())
                .locationId(result.locationId())
                .locationName(result.locationName())
                .cityName(result.cityName())
                .imageUrl(result.imageUrl())
                .totalVerificationCount(result.totalVerificationCount())
                .build();
    }
}
