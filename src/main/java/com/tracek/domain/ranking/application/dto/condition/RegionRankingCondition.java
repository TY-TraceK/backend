package com.tracek.domain.ranking.application.dto.condition;

import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import lombok.Builder;

@Builder
public record RegionRankingCondition(int limit) {

    public RankingSearchCriteria<String> toCriteria() {
        return new RankingSearchCriteria<String>(null, null, limit);
    }
}
