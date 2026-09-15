package com.tracek.domain.ranking.presentaion.dto.response;

import com.tracek.domain.ranking.application.dto.result.RankingTopResult;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public record RankingTopResponse<T>(List<T> rankings, int totalCount, LocalDateTime lastUpdateAt) {

    public static <R, T> RankingTopResponse<T> from(
            RankingTopResult<R> result, Function<R, T> mapper) {
        List<T> mappedRankings = result.rankings().stream().map(mapper).toList();

        return new RankingTopResponse<>(mappedRankings, result.totalCount(), result.lastUpdateAt());
    }
}
