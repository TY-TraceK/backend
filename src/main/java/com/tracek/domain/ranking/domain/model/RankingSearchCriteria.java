package com.tracek.domain.ranking.domain.model;

public record RankingSearchCriteria<T>(Long lastCount, T lastKey, Integer limit) {}
