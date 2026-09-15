package com.tracek.domain.ranking.application.dto.condition;

import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import lombok.Builder;

@Builder
public record RegionRankingCondition(Integer topN) {

    public RankingSearchCriteria<String> toCriteria() {
        return RankingSearchCriteria.<String>builder().limit(topN == null ? 10 : topN).build();
    }
}
