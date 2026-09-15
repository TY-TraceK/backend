package com.tracek.domain.ranking.application.dto.condition;

import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import lombok.Builder;

@Builder
public record LocationRankingCondition(String categoryName, String cityName, Integer topN) {

    public RankingSearchCriteria<Long> toCriteria() {
        return RankingSearchCriteria.<Long>builder()
                .categoryName(categoryName)
                .cityName(cityName)
                .limit(topN == null ? 10 : topN)
                .build();
    }
}
