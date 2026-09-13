package com.tracek.domain.ranking.application.dto.result;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.TargetId;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RankingSliceResultTest {

    @Test
    @DisplayName("조회 결과가 size보다 많으면 size만큼 자르고 hasNext를 true로 반환한다")
    void hasNext() {
        // given
        List<RankingItem> items = List.of(item(1L, 100L), item(2L, 90L), item(3L, 80L));

        // when
        RankingSliceResult<Long> result =
                RankingSliceResult.from(
                        items,
                        2,
                        item -> item.targetId().artistId(),
                        item -> item.targetId().artistId());

        // then
        assertThat(result.rankings()).containsExactly(1L, 2L);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.lastCount()).isEqualTo(90L);
        assertThat(result.lastId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("조회 결과가 size 이하이면 전체를 반환하고 hasNext는 false이다")
    void noNext() {
        // given
        List<RankingItem> items = List.of(item(1L, 100L), item(2L, 90L));

        // when
        RankingSliceResult<Long> result =
                RankingSliceResult.from(
                        items,
                        2,
                        item -> item.targetId().artistId(),
                        item -> item.targetId().artistId());

        // then
        assertThat(result.rankings()).containsExactly(1L, 2L);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.lastCount()).isEqualTo(90L);
        assertThat(result.lastId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("조회 결과가 비어 있으면 커서를 null로 반환한다")
    void empty() {
        // when
        RankingSliceResult<Long> result =
                RankingSliceResult.from(
                        List.of(),
                        10,
                        item -> item.targetId().artistId(),
                        item -> item.targetId().artistId());

        // then
        assertThat(result.rankings()).isEmpty();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.lastCount()).isNull();
        assertThat(result.lastId()).isNull();
    }

    @Test
    @DisplayName("size가 null이면 페이징하지 않고 전체 결과를 반환한다")
    void noPaging() {
        // given
        List<RankingItem> items = List.of(item(1L, 100L), item(2L, 90L), item(3L, 80L));

        // when
        RankingSliceResult<Long> result =
                RankingSliceResult.from(
                        items,
                        null,
                        item -> item.targetId().artistId(),
                        item -> item.targetId().artistId());

        // then
        assertThat(result.rankings()).containsExactly(1L, 2L, 3L);
        assertThat(result.hasNext()).isFalse();
        assertThat(result.lastCount()).isEqualTo(80L);
        assertThat(result.lastId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("size가 null이고 결과도 비어 있으면 빈 Slice를 반환한다")
    void noPagingAndEmpty() {
        // when
        RankingSliceResult<Long> result =
                RankingSliceResult.from(
                        List.of(),
                        null,
                        item -> item.targetId().artistId(),
                        item -> item.targetId().artistId());

        // then
        assertThat(result.rankings()).isEmpty();
        assertThat(result.hasNext()).isFalse();
        assertThat(result.lastCount()).isNull();
        assertThat(result.lastId()).isNull();
    }

    private RankingItem item(Long artistId, Long count) {
        return new RankingItem(new TargetId(null, null, artistId), count);
    }
}
