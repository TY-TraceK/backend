package com.tracek.domain.ranking.infrastructure.persistence.nativequery;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

import com.tracek.domain.ranking.domain.model.LocationRankingView;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import jakarta.persistence.EntityManager;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(LocationVisitRankingNativeRepository.class)
class LocationVisitRankingNativeRepositoryTest {

    @Autowired private LocationVisitRankingNativeRepository repository;

    @Autowired private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("DELETE FROM location_ranking").executeUpdate();

        entityManager.createNativeQuery("DELETE FROM location").executeUpdate();
    }

    private void saveCity(Long locationId, String city, long verificationCount) {

        saveLocation(locationId, city, city + " 테스트 주소");

        saveRanking(locationId, verificationCount);
    }

    private void saveLocation(Long id, String city, String address) {

        entityManager
                .createNativeQuery(
                        """
                INSERT INTO location (
                    id,
                    city,
                    address
                )
                VALUES (
                    :id,
                    :city,
                    :address
                )
                """)
                .setParameter("id", id)
                .setParameter("city", city)
                .setParameter("address", address)
                .executeUpdate();
    }

    private void saveRanking(Long locationId, long count) {

        entityManager
                .createNativeQuery(
                        """
                INSERT INTO location_ranking (
                    location_id,
                    total_visit_verification_count,
                    updated_at
                )
                VALUES (
                    :locationId,
                    :count,
                    CURRENT_TIMESTAMP
                )
                """)
                .setParameter("locationId", locationId)
                .setParameter("count", count)
                .executeUpdate();
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    String cityName(int rank) {
        return String.format("테스트도시%02d", rank);
    }

    @Nested
    @DisplayName("지역별 방문 인증 수 집계 및 순위 산정 테스트")
    class RankingAndAggregationTest {

        @Test
        @DisplayName("같은 지역에 속한 관광지의 방문 인증 수를 합산한다")
        void sumVerificationCountByCity() {
            // given
            saveLocation(1L, "부산광역시", "해운대구");
            saveLocation(2L, "부산광역시", "수영구");
            saveLocation(3L, "부산광역시", "부산진구");

            saveLocation(4L, "서울특별시", "강남구");

            saveRanking(1L, 100L);
            saveRanking(2L, 200L);
            saveRanking(3L, 300L);

            saveRanking(4L, 500L);

            flushAndClear();

            // when
            RankingSearchCriteria<String> criteria = new RankingSearchCriteria<>(null, null, 20);
            List<LocationRankingView> result = repository.findTopRankings(criteria);

            // then
            assertThat(result).hasSize(2);

            LocationRankingView busan =
                    result.stream()
                            .filter(it -> it.cityName().equals("부산광역시"))
                            .findFirst()
                            .orElseThrow();

            assertThat(busan.totalVerificationCount()).isEqualTo(600L);
            assertThat(busan.locationId()).isNull();
            assertThat(busan.locationAddress()).isNull();
        }

        @Test
        @DisplayName("지역별 방문 인증 합계가 높은 순서대로 순위를 매긴다")
        void rankByTotalVerificationCount() {
            // given
            saveCity(1L, "부산광역시", 1000L);
            saveCity(2L, "서울특별시", 3000L);
            saveCity(3L, "대구광역시", 2000L);
            saveCity(4L, "광주광역시", 500L);

            flushAndClear();

            // when
            RankingSearchCriteria<String> criteria = new RankingSearchCriteria<>(null, null, 20);
            List<LocationRankingView> result = repository.findTopRankings(criteria);

            // then
            assertThat(result)
                    .extracting(
                            LocationRankingView::rank,
                            LocationRankingView::cityName,
                            LocationRankingView::totalVerificationCount)
                    .containsExactly(
                            tuple(1, "서울특별시", 3000L),
                            tuple(2, "대구광역시", 2000L),
                            tuple(3, "부산광역시", 1000L),
                            tuple(4, "광주광역시", 500L));
        }

        @Test
        @DisplayName("같은 지역에 여러 관광지가 존재해도 하나의 지역 랭킹으로 집계한다")
        void groupMultipleLocationsIntoSingleCityRanking() {
            // given
            saveLocation(1L, "부산광역시", "해운대구");
            saveLocation(2L, "부산광역시", "수영구");
            saveLocation(3L, "부산광역시", "부산진구");

            saveLocation(4L, "대구광역시", "달서구");
            saveLocation(5L, "대구광역시", "수성구");

            saveRanking(1L, 100L);
            saveRanking(2L, 200L);
            saveRanking(3L, 300L);

            saveRanking(4L, 250L);
            saveRanking(5L, 150L);

            flushAndClear();

            // when
            RankingSearchCriteria<String> criteria = new RankingSearchCriteria<>(null, null, 20);
            List<LocationRankingView> result = repository.findTopRankings(criteria);

            // then
            assertThat(result).hasSize(2);

            assertThat(result)
                    .extracting(
                            LocationRankingView::rank,
                            LocationRankingView::cityName,
                            LocationRankingView::totalVerificationCount)
                    .containsExactly(tuple(1, "부산광역시", 600L), tuple(2, "대구광역시", 400L));
        }
    }

    @Nested
    @DisplayName("TOP N 순위 제한 및 동점자(Rank) 처리 테스트")
    class TopLimitAndRankingPolicyTest {

        @Test
        @DisplayName("요청한 순위(TOP N) 이하의 데이터를 가져오며, 동점자가 존재하면 모두 포함한다")
        void limitTopRankingsByRankCondition() {
            // given
            // 1위: 테스트도시03 (300)
            // 2위(동점): 테스트도시01 (200), 테스트도시02 (200)
            // 4위: 테스트도시04 (100)
            saveCity(1L, "테스트도시01", 200L);
            saveCity(2L, "테스트도시02", 200L);
            saveCity(3L, "테스트도시03", 300L);
            saveCity(4L, "테스트도시04", 100L);

            flushAndClear();

            // when (TOP 2까지 요청 -> 1위와 공동 2위인 도시들이 모두 포함되어야 함)
            RankingSearchCriteria<String> criteria = new RankingSearchCriteria<>(null, null, 2);
            List<LocationRankingView> result = repository.findTopRankings(criteria);

            // then
            // 3위 제한이 아니라 2위 이하 조건이므로, 1위 1개 + 공동 2위 2개 = 총 3개가 조회되어야 함
            assertThat(result).hasSize(3);
            assertThat(result)
                    .extracting(LocationRankingView::cityName, LocationRankingView::rank)
                    .containsExactly(tuple("테스트도시03", 1), tuple("테스트도시01", 2), tuple("테스트도시02", 2));
        }

        @Test
        @DisplayName("동일한 방문 인증 수를 가진 지역은 동일한 순위를 가지며 다음 순위는 건너 뛴다")
        void sameCountHasSameRankWithGaps() {
            // given
            saveCity(1L, "서울특별시", 1000L);

            saveCity(2L, "부산광역시", 800L);
            saveCity(3L, "대구광역시", 800L);
            saveCity(4L, "광주광역시", 800L);

            saveCity(5L, "대전광역시", 500L);

            flushAndClear();

            // when
            RankingSearchCriteria<String> criteria = new RankingSearchCriteria<>(null, null, 20);
            List<LocationRankingView> result = repository.findTopRankings(criteria);

            // then
            assertThat(result)
                    .extracting(
                            LocationRankingView::cityName,
                            LocationRankingView::rank,
                            LocationRankingView::totalVerificationCount)
                    .containsExactly(
                            tuple("서울특별시", 1, 1000L),
                            tuple("광주광역시", 2, 800L),
                            tuple("대구광역시", 2, 800L),
                            tuple("부산광역시", 2, 800L),
                            tuple("대전광역시", 5, 500L));
        }
    }

    @Nested
    @DisplayName("최신 수정 시간(lastUpdateAt) 집계 테스트")
    class LastUpdatedAtTest {

        @Test
        @DisplayName("지역별로 그룹화할 때 해당 지역 내 관광지 중 가장 최신 수정 시간을 반영한다")
        void calculateLatestUpdatedAtPerCity() {
            // given
            saveLocation(1L, "부산광역시", "해운대구");
            saveLocation(2L, "부산광역시", "수영구");

            LocalDateTime olderTime = LocalDateTime.of(2026, 1, 1, 10, 0, 0);
            LocalDateTime newerTime = LocalDateTime.of(2026, 1, 1, 10, 30, 0);

            entityManager
                    .createNativeQuery(
                            """
                  INSERT INTO location_ranking (location_id, total_visit_verification_count, updated_at)
                  VALUES (:locationId, :count, :updatedAt)
                  """)
                    .setParameter("locationId", 1L)
                    .setParameter("count", 100L)
                    .setParameter("updatedAt", Timestamp.valueOf(olderTime))
                    .executeUpdate();

            entityManager
                    .createNativeQuery(
                            """
                  INSERT INTO location_ranking (location_id, total_visit_verification_count, updated_at)
                  VALUES (:locationId, :count, :updatedAt)
                  """)
                    .setParameter("locationId", 2L)
                    .setParameter("count", 200L)
                    .setParameter("updatedAt", Timestamp.valueOf(newerTime))
                    .executeUpdate();

            flushAndClear();

            // when
            RankingSearchCriteria<String> criteria = new RankingSearchCriteria<>(null, null, 20);
            List<LocationRankingView> result = repository.findTopRankings(criteria);

            // then
            assertThat(result).hasSize(1);
            LocationRankingView busan = result.getFirst();

            assertThat(busan.cityName()).isEqualTo("부산광역시");
            assertThat(busan.lastUpdateAt()).isEqualTo(newerTime);
        }
    }
}
