package com.tracek.domain.ranking.application.dto.result;

import com.tracek.domain.ranking.domain.model.LocationRankingView;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public record RankingTopResult<T>(List<T> rankings, int totalCount, LocalDateTime lastUpdateAt) {

    public static <T> RankingTopResult<T> from(
            List<LocationRankingView> items, Function<LocationRankingView, T> mapper) {
        List<T> results = items.stream().map(mapper).toList();

        if (items.isEmpty()) {
            return new RankingTopResult<>(results, results.size(), LocalDateTime.now());
        }

        LocalDateTime lastUpdateAt =
                items.stream()
                        .map(LocationRankingView::lastUpdateAt)
                        .filter(java.util.Objects::nonNull) // null 값 필터링 추가
                        .max(LocalDateTime::compareTo)
                        .orElse(
                                LocalDateTime
                                        .now()); // 모두 null이거나 값이 없을 경우의 기본값 처리 (.get() 대신 안전하게 사용)

        return new RankingTopResult<>(results, results.size(), lastUpdateAt);
    }
}
