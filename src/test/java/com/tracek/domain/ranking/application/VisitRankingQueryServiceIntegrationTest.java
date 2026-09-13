package com.tracek.domain.ranking.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.application.dto.result.RelatedArtistRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedContentRankingResult;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import com.tracek.domain.ranking.domain.model.ContentArtistVisitRanking;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.ContentArtistVisitRankingJpaRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class VisitRankingQueryServiceIntegrationTest {

    @Autowired private VisitRankingQueryService visitRankingQueryService;

    @Autowired
    private ContentArtistVisitRankingJpaRepository contentArtistVisitRankingJpaRepository;

    @Autowired private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        contentArtistVisitRankingJpaRepository.deleteAll();

        contentArtistVisitRankingJpaRepository.saveAll(
                List.of(
                        // contentId = 100
                        createRanking(100L, 1001L, 100L),
                        createRanking(100L, 1002L, 90L),
                        createRanking(100L, 1003L, 90L),
                        createRanking(100L, 1004L, 80L),

                        // contentId = 200
                        createRanking(200L, 1001L, 70L),
                        createRanking(200L, 1002L, 60L),

                        // contentId = 300
                        createRanking(300L, 1001L, 50L),

                        // 조회 대상과 관계없는 데이터
                        createRanking(999L, 9999L, 999L)));

        entityManager.flush();
        entityManager.clear();
    }

    private ContentArtistVisitRanking createRanking(Long contentId, Long artistId, Long count) {
        ContentArtistVisitRanking ranking = ContentArtistVisitRanking.create(contentId, artistId);

        for (long i = 0; i < count; i++) {
            ranking.increaseVerificationCount();
        }

        return ranking;
    }

    @Nested
    @DisplayName("콘텐츠 기준 아티스트 랭킹 조회")
    class GetArtistsByContent {

        @Test
        @DisplayName("contentId로 조회하면 해당 콘텐츠의 artistId가 정확히 반환된다")
        void getArtistsByContent() {
            // given
            RankingCondition condition = new RankingCondition(null, null, 3);

            // when
            RankingSliceResult<RelatedArtistRankingResult> result =
                    visitRankingQueryService.getArtistsByContent(100L, condition);

            // then
            assertThat(result.rankings())
                    .extracting(
                            RelatedArtistRankingResult::artistId,
                            RelatedArtistRankingResult::totalVerificationCount)
                    .containsExactly(tuple(1001L, 100L), tuple(1002L, 90L), tuple(1003L, 90L));

            assertThat(result.hasNext()).isTrue();

            assertThat(result.lastCount()).isEqualTo(90L);
            assertThat(result.lastId()).isEqualTo(1003L);
        }

        @Test
        @DisplayName("artistId 자리에 contentId가 잘못 들어가지 않는다")
        void artistIdShouldNotBeContentId() {
            // given
            RankingCondition condition = new RankingCondition(null, null, 10);

            // when
            RankingSliceResult<RelatedArtistRankingResult> result =
                    visitRankingQueryService.getArtistsByContent(100L, condition);

            // then
            assertThat(result.rankings())
                    .extracting(RelatedArtistRankingResult::artistId)
                    .containsExactly(1001L, 1002L, 1003L, 1004L);

            // contentId = 100L이 artistId로 잘못 projection 되면 실패
            assertThat(result.rankings())
                    .extracting(RelatedArtistRankingResult::artistId)
                    .doesNotContain(100L);
        }

        @Test
        @DisplayName("다른 contentId의 아티스트 랭킹은 섞이지 않는다")
        void shouldFilterByContentId() {
            // given
            RankingCondition condition = new RankingCondition(null, null, 10);

            // when
            RankingSliceResult<RelatedArtistRankingResult> result =
                    visitRankingQueryService.getArtistsByContent(200L, condition);

            // then
            assertThat(result.rankings())
                    .extracting(
                            RelatedArtistRankingResult::artistId,
                            RelatedArtistRankingResult::totalVerificationCount)
                    .containsExactly(tuple(1001L, 70L), tuple(1002L, 60L));
        }

        @Test
        @DisplayName("동일 득표 수에서는 artistId 오름차순으로 정렬된다")
        void orderByArtistIdWhenCountIsSame() {
            // given
            RankingCondition condition = new RankingCondition(null, null, 10);

            // when
            RankingSliceResult<RelatedArtistRankingResult> result =
                    visitRankingQueryService.getArtistsByContent(100L, condition);

            // then
            assertThat(result.rankings())
                    .extracting(RelatedArtistRankingResult::artistId)
                    .containsExactly(1001L, 1002L, 1003L, 1004L);
        }

        @Test
        @DisplayName("artistId를 커서로 사용해 다음 페이지를 조회한다")
        void getNextPageByArtistCursor() {
            // given
            RankingCondition condition = new RankingCondition(90L, 1002L, 2);

            // when
            RankingSliceResult<RelatedArtistRankingResult> result =
                    visitRankingQueryService.getArtistsByContent(100L, condition);

            // then
            assertThat(result.rankings())
                    .extracting(
                            RelatedArtistRankingResult::artistId,
                            RelatedArtistRankingResult::totalVerificationCount)
                    .containsExactly(tuple(1003L, 90L), tuple(1004L, 80L));

            assertThat(result.hasNext()).isFalse();
            assertThat(result.lastCount()).isEqualTo(80L);
            assertThat(result.lastId()).isEqualTo(1004L);
        }
    }

    @Nested
    @DisplayName("아티스트 기준 콘텐츠 랭킹 조회")
    class GetContentsByArtist {

        @Test
        @DisplayName("artistId로 조회하면 해당 아티스트의 contentId가 정확히 반환된다")
        void getContentsByArtist() {
            // given
            RankingCondition condition = new RankingCondition(null, null, 10);

            // when
            RankingSliceResult<RelatedContentRankingResult> result =
                    visitRankingQueryService.getContentsByArtist(1001L, condition);

            // then
            assertThat(result.rankings())
                    .extracting(
                            RelatedContentRankingResult::contentId,
                            RelatedContentRankingResult::totalVerificationCount)
                    .containsExactly(tuple(100L, 100L), tuple(200L, 70L), tuple(300L, 50L));

            assertThat(result.hasNext()).isFalse();

            assertThat(result.lastCount()).isEqualTo(50L);
            assertThat(result.lastId()).isEqualTo(300L);
        }

        @Test
        @DisplayName("contentId 자리에 artistId가 잘못 들어가지 않는다")
        void contentIdShouldNotBeArtistId() {
            // given
            RankingCondition condition = new RankingCondition(null, null, 10);

            // when
            RankingSliceResult<RelatedContentRankingResult> result =
                    visitRankingQueryService.getContentsByArtist(1001L, condition);

            // then
            assertThat(result.rankings())
                    .extracting(RelatedContentRankingResult::contentId)
                    .containsExactly(100L, 200L, 300L);

            // artistId = 1001L이 contentId로 잘못 projection 되면 실패
            assertThat(result.rankings())
                    .extracting(RelatedContentRankingResult::contentId)
                    .doesNotContain(1001L);
        }

        @Test
        @DisplayName("다른 artistId의 콘텐츠는 섞이지 않는다")
        void shouldFilterByArtistId() {
            // given
            RankingCondition condition = new RankingCondition(null, null, 10);

            // when
            RankingSliceResult<RelatedContentRankingResult> result =
                    visitRankingQueryService.getContentsByArtist(1002L, condition);

            // then
            assertThat(result.rankings())
                    .extracting(
                            RelatedContentRankingResult::contentId,
                            RelatedContentRankingResult::totalVerificationCount)
                    .containsExactly(tuple(100L, 90L), tuple(200L, 60L));
        }

        @Test
        @DisplayName("contentId를 커서로 사용해 다음 페이지를 조회한다")
        void getNextPageByContentCursor() {
            // given
            RankingCondition condition = new RankingCondition(70L, 200L, 2);

            // when
            RankingSliceResult<RelatedContentRankingResult> result =
                    visitRankingQueryService.getContentsByArtist(1001L, condition);

            // then
            assertThat(result.rankings())
                    .extracting(
                            RelatedContentRankingResult::contentId,
                            RelatedContentRankingResult::totalVerificationCount)
                    .containsExactly(tuple(300L, 50L));

            assertThat(result.hasNext()).isFalse();
            assertThat(result.lastCount()).isEqualTo(50L);
            assertThat(result.lastId()).isEqualTo(300L);
        }
    }
}
