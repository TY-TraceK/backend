package com.tracek.domain.ranking.presentaion.dto.request;

import com.tracek.domain.ranking.application.dto.condition.RegionRankingCondition;

public record RegionRankingRequest(Integer topN) {

    public RegionRankingCondition toCondition() {
        return RegionRankingCondition.builder().topN(topN).build();
    }
}
