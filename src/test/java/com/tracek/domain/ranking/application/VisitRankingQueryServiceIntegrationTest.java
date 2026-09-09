package com.tracek.domain.ranking.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.application.dto.result.RelatedArtistRankingResult;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import com.tracek.domain.ranking.domain.model.ContentArtistVisitRanking;
import com.tracek.domain.ranking.infrastructure.persistence.ContentArtistVisitRankingJpaRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class VisitRankingQueryServiceIntegrationTest {

    @Autowired private VisitRankingQueryService VisitRankingQueryService;

    @Autowired
    private ContentArtistVisitRankingJpaRepository contentArtistVisitRankingJpaRepository;

    @Autowired private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        contentArtistVisitRankingJpaRepository.deleteAll();

        contentArtistVisitRankingJpaRepository.saveAll(
                List.of(
                        createRanking(1L, 10L, 100L),
                        createRanking(1L, 20L, 90L),
                        createRanking(1L, 30L, 90L),
                        createRanking(1L, 40L, 80L),

                        // 다른 콘텐츠 데이터
                        createRanking(2L, 50L, 999L)));

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("콘텐츠 기준으로 연관 아티스트 랭킹을 실제 DB에서 조회한다")
    void getArtistsByContent() {
        // given
        RankingCondition condition = new RankingCondition(null, null, 3);

        // when
        RankingSliceResult<RelatedArtistRankingResult> result =
                VisitRankingQueryService.getArtistsByContent(1L, condition);

        // then
        assertThat(result.rankings())
                .extracting(
                        RelatedArtistRankingResult::artistId,
                        RelatedArtistRankingResult::totalVerificationCount)
                .containsExactly(tuple(10L, 100L), tuple(20L, 90L), tuple(30L, 90L));

        assertThat(result.hasNext()).isTrue();

        assertThat(result.lastCount()).isEqualTo(90L);
        assertThat(result.lastId()).isEqualTo(30L);
    }

    private ContentArtistVisitRanking createRanking(Long contentId, Long artistId, Long count) {
        ContentArtistVisitRanking ranking = ContentArtistVisitRanking.create(contentId, artistId);

        for (long i = 0; i < count; i++) {
            ranking.increaseVerificationCount();
        }

        return ranking;
    }
}
