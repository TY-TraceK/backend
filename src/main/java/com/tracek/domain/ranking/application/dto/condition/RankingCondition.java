package com.tracek.domain.ranking.application.dto.condition;

public record RankingCondition(Long lastCount, Long lastId, int size) {

    public RankingCondition {

        if (size <= 0) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }
    }
}
