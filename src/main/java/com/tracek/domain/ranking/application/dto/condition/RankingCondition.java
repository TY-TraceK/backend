package com.tracek.domain.ranking.application.dto.condition;

import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;

public record RankingCondition(Long lastCount, Long lastId, int size) {

    public RankingCondition {

        if (size <= 0) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }
    }

    public RankingSearchCriteria<Long> toCriteria() {
        return RankingSearchCriteria.<Long>builder()
                .lastCount(lastCount)
                .lastKey(lastId)
                .limit(size)
                .build();
    }
}
