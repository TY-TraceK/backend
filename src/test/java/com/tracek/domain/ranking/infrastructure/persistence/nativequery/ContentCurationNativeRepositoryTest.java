package com.tracek.domain.ranking.infrastructure.persistence.nativequery;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@Import(ContentCurationNativeRepository.class)
class ContentCurationNativeRepositoryTest {

    @Autowired private ContentCurationNativeRepository repository;
    @Autowired private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        entityManager.createNativeQuery("DELETE FROM content_location_ranking").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM location").executeUpdate();
        entityManager.createNativeQuery("DELETE FROM content").executeUpdate();
    }

    @Test
    @DisplayName("방문 인증 합계가 가장 적은 콘텐츠와 인증 수가 많은 여행지 3곳을 조회한다")
    void findLowVisitContentCuration() {
        saveContent(1L, "많이 방문한 콘텐츠");
        saveContent(2L, "덜 방문한 콘텐츠");

        saveLocation(11L, "여행지 A");
        saveLocation(12L, "여행지 B");
        saveLocation(13L, "여행지 C");
        saveLocation(14L, "여행지 D");
        saveLocation(21L, "다른 여행지");

        saveRanking(11L, 2L, 5L);
        saveRanking(12L, 2L, 20L);
        saveRanking(13L, 2L, 10L);
        saveRanking(14L, 2L, 1L);
        saveRanking(21L, 1L, 100L);

        entityManager.flush();
        entityManager.clear();

        var result = repository.findLowVisitContentCuration().orElseThrow();

        assertThat(result.contentId()).isEqualTo(2L);
        assertThat(result.contentTitle()).isEqualTo("덜 방문한 콘텐츠");
        assertThat(result.totalVerificationCount()).isEqualTo(36L);
        assertThat(result.locationNames()).containsExactly("여행지 B", "여행지 C", "여행지 A");
    }

    @Test
    @DisplayName("방문 인증 랭킹 데이터가 없으면 빈 결과를 반환한다")
    void returnEmptyWhenRankingDoesNotExist() {
        assertThat(repository.findLowVisitContentCuration()).isEmpty();
    }

    private void saveContent(Long id, String title) {
        entityManager
                .createNativeQuery("INSERT INTO content (id, title) VALUES (:id, :title)")
                .setParameter("id", id)
                .setParameter("title", title)
                .executeUpdate();
    }

    private void saveLocation(Long id, String name) {
        entityManager
                .createNativeQuery(
                        "INSERT INTO location (id, name, city, address) VALUES (:id, :name, '부산광역시', '테스트 주소')")
                .setParameter("id", id)
                .setParameter("name", name)
                .executeUpdate();
    }

    private void saveRanking(Long locationId, Long contentId, long count) {
        entityManager
                .createNativeQuery(
                        """
                        INSERT INTO content_location_ranking (
                            location_id,
                            content_id,
                            total_visit_verification_count
                        )
                        VALUES (:locationId, :contentId, :count)
                        """)
                .setParameter("locationId", locationId)
                .setParameter("contentId", contentId)
                .setParameter("count", count)
                .executeUpdate();
    }
}
