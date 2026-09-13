package com.tracek.domain.ranking.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ContentArtistLocationVisitRankingTest {

    @Test
    void createAndChangeVerificationCount() {
        ContentArtistLocationVisitRanking ranking =
                ContentArtistLocationVisitRanking.create(1L, 2L, 3L);

        assertThat(ranking.getContentId()).isEqualTo(1L);
        assertThat(ranking.getLocationId()).isEqualTo(2L);
        assertThat(ranking.getArtistId()).isEqualTo(3L);
        assertThat(ranking.getTotalVerificationCount()).isZero();

        ranking.increaseVerificationCount();

        assertThat(ranking.getTotalVerificationCount()).isEqualTo(1L);

        ranking.decreaseVerificationCount();

        assertThat(ranking.getTotalVerificationCount()).isZero();
    }

    @Test
    void decreaseDoesNotGoBelowZero() {
        ContentArtistLocationVisitRanking ranking =
                ContentArtistLocationVisitRanking.create(1L, 2L, 3L);

        ranking.decreaseVerificationCount();

        assertThat(ranking.getTotalVerificationCount()).isZero();
    }
}
