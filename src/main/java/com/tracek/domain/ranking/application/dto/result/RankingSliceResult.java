package com.tracek.domain.ranking.application.dto.result;

import java.util.List;

public record RankingSliceResult<T>(
        List<T> rankings, Long lastCount, Long lastId, boolean hasNext) {}
