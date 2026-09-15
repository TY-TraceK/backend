package com.tracek.domain.ranking.presentaion.dto.request;

import com.tracek.domain.ranking.application.dto.condition.LocationRankingCondition;

public record LocationRankingRequest(String category, String city, Integer topN) {

    public LocationRankingCondition toCondition() {
        return LocationRankingCondition.builder()
                .categoryName(category)
                .cityName(city)
                .topN(topN)
                .build();
    }
}
