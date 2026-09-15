package com.tracek.domain.ranking.domain.model;

import lombok.Builder;

@Builder
public record RankingSearchCriteria<T>(
        Long lastCount,
        T lastKey,
        TargetId targetId,
        Integer limit,
        String cityName,
        String categoryName) {}
