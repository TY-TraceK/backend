package com.tracek.domain.ranking.application.dto.result;

import com.tracek.domain.ranking.domain.model.RankingItem;
import java.util.List;
import java.util.function.Function;

public record RankingSliceResult<T>(
        List<T> rankings, Long lastCount, Long lastId, boolean hasNext) {

    public static <T> RankingSliceResult<T> from(
            List<RankingItem> items,
            Integer size,
            Function<RankingItem, T> mapper,
            Function<RankingItem, Long> idExtractor) {

        if (size == null) {
            List<T> results = items.stream().map(mapper).toList();
            if (items.isEmpty()) {
                return new RankingSliceResult<>(results, null, null, false);
            }
            RankingItem last = items.getLast();
            return new RankingSliceResult<>(
                    results, last.totalVerificationCount(), idExtractor.apply(last), false);
        }

        boolean hasNext = items.size() > size;

        List<RankingItem> slicedItems = hasNext ? items.subList(0, size) : items;

        List<T> results = slicedItems.stream().map(mapper).toList();

        if (slicedItems.isEmpty()) {
            return new RankingSliceResult<>(results, null, null, false);
        }

        RankingItem last = slicedItems.getLast();

        return new RankingSliceResult<>(
                results, last.totalVerificationCount(), idExtractor.apply(last), hasNext);
    }
}
