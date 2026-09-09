package com.tracek.domain.ranking.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.location.application.dto.LocationContentArtistResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.ranking.domain.model.ArtistLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.ContentLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.LocationVisitRanking;
import com.tracek.domain.ranking.domain.repository.ArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.LocationVisitRankingRepository;
import com.tracek.domain.ranking.infrastructure.persistence.ArtistLocationVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.ContentLocationVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.LocationVisitRankingJpaRepository;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCancelCommand;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCreateCommand;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCreateResult;
import com.tracek.domain.visitVerification.application.service.VisitVerificationCommandService;
import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.infrastructure.persistence.VisitVerificationJpaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class VisitVerificationRankingIntegrationTest {

    private static final Double LATITUDE = 35.123456;
    private static final Double LONGITUDE = 128.123456;

    private static final int CONCURRENT_REQUEST_COUNT = 10;

    @Autowired private VisitVerificationCommandService visitVerificationCommandService;

    @Autowired private VisitVerificationJpaRepository visitVerificationRepository;

    @Autowired private LocationVisitRankingRepository locationVisitRankingRepository;

    @Autowired private ArtistLocationVisitRankingRepository artistLocationVisitRankingRepository;

    @Autowired private ContentLocationVisitRankingRepository contentLocationVisitRankingRepository;

    /*
     * 테스트 데이터 초기화용
     */
    @Autowired private LocationVisitRankingJpaRepository locationVisitRankingJpaRepository;

    @Autowired
    private ArtistLocationVisitRankingJpaRepository artistLocationVisitRankingJpaRepository;

    @Autowired
    private ContentLocationVisitRankingJpaRepository contentLocationVisitRankingJpaRepository;

    /*
     * VisitVerification 외부 조회만 Mock
     */
    @MockitoBean private LocationQueryService locationQueryService;

    private Long userId;

    private Long locationId;
    private Long locationContentArtistId;

    private Long artistId;
    private Long contentId;

    @BeforeEach
    void setUp() {
        userId = 1L;

        locationId = 100L;
        locationContentArtistId = 1000L;

        artistId = 10L;
        contentId = 20L;

        LocationContentArtistResult mockResult =
                org.mockito.Mockito.mock(LocationContentArtistResult.class);

        given(mockResult.getLocationId()).willReturn(locationId);

        given(mockResult.getArtistId()).willReturn(artistId);

        given(mockResult.getContentId()).willReturn(contentId);

        given(locationQueryService.getMappingById(anyLong())).willReturn(mockResult);
    }

    @AfterEach
    void tearDown() {
        contentLocationVisitRankingJpaRepository.deleteAllInBatch();
        artistLocationVisitRankingJpaRepository.deleteAllInBatch();
        locationVisitRankingJpaRepository.deleteAllInBatch();

        visitVerificationRepository.deleteAllInBatch();
    }

    /*
     * ============================================================
     * 생성
     * ============================================================
     */

    private VisitVerificationCreateCommand createCommand(Long userId) {

        return VisitVerificationCreateCommand.builder()
                .userId(userId)
                .locationId(locationId)
                .locationContentArtistId(locationContentArtistId)
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .build();
    }

    /*
     * ============================================================
     * 취소
     * ============================================================
     */

    private LocationVisitRanking getLocationRanking() {

        return locationVisitRankingRepository.findByLocationId(locationId).orElseThrow();
    }

    /*
     * ============================================================
     * Helper
     * ============================================================
     */

    private ArtistLocationVisitRanking getArtistRanking() {

        return artistLocationVisitRankingRepository
                .findByLocationIdAndArtistId(locationId, artistId)
                .orElseThrow();
    }

    private ContentLocationVisitRanking getContentRanking() {

        return contentLocationVisitRankingRepository
                .findByLocationIdAndContentId(locationId, contentId)
                .orElseThrow();
    }

    private void assertRankingCount(long expectedCount) {

        assertThat(getLocationRanking().getTotalVerificationCount()).isEqualTo(expectedCount);

        assertThat(getArtistRanking().getTotalVerificationCount()).isEqualTo(expectedCount);

        assertThat(getContentRanking().getTotalVerificationCount()).isEqualTo(expectedCount);
    }

    @Nested
    @DisplayName("방문 인증 생성 → 랭킹 증가")
    class CreateVisitVerificationRankingTest {

        @Test
        @DisplayName("방문 인증 1건이 생성되면 관광지/아티스트/콘텐츠 랭킹이 모두 1 증가한다")
        void createVisitVerificationReflectsRanking() {

            // given
            VisitVerificationCreateCommand command = createCommand(userId);

            // when
            visitVerificationCommandService.createVisitVerification(command);

            // then
            long originalCount = visitVerificationRepository.countByLocationId(locationId);

            LocationVisitRanking locationRanking = getLocationRanking();

            ArtistLocationVisitRanking artistRanking = getArtistRanking();

            ContentLocationVisitRanking contentRanking = getContentRanking();

            assertThat(originalCount).isEqualTo(1L);

            assertThat(locationRanking.getTotalVerificationCount()).isEqualTo(1L);

            assertThat(artistRanking.getTotalVerificationCount()).isEqualTo(1L);

            assertThat(contentRanking.getTotalVerificationCount()).isEqualTo(1L);
        }

        @Test
        @DisplayName("여러 사용자가 동시에 동일 조합을 방문 인증해도 원본 수와 랭킹 집계 수가 일치한다")
        void concurrentCreateKeepsRankingConsistency() throws InterruptedException {

            // given
            int threadCount = CONCURRENT_REQUEST_COUNT;

            CountDownLatch startLatch = new CountDownLatch(1);

            CountDownLatch endLatch = new CountDownLatch(threadCount);

            AtomicInteger successCount = new AtomicInteger();

            AtomicInteger failCount = new AtomicInteger();

            /*
             * 동일 관광지/콘텐츠/아티스트 조합에
             * 서로 다른 사용자 10명이 동시에 방문 인증
             */
            try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {

                for (int i = 0; i < threadCount; i++) {

                    Long concurrentUserId = 100L + i;

                    executorService.submit(
                            () -> {
                                try {
                                    startLatch.await();

                                    VisitVerificationCreateCommand command =
                                            createCommand(concurrentUserId);

                                    visitVerificationCommandService.createVisitVerification(
                                            command);

                                    successCount.incrementAndGet();

                                } catch (Throwable throwable) {

                                    failCount.incrementAndGet();

                                    System.err.println(
                                            "방문 인증 생성 실패: "
                                                    + throwable.getClass().getSimpleName()
                                                    + " - "
                                                    + throwable.getMessage());

                                } finally {
                                    endLatch.countDown();
                                }
                            });
                }

                // 모든 스레드 동시에 시작
                startLatch.countDown();

                // 모든 스레드 종료 대기
                endLatch.await();
            }

            // then
            long originalCount = visitVerificationRepository.countByLocationId(locationId);

            long locationRankingCount = getLocationRanking().getTotalVerificationCount();

            long artistRankingCount = getArtistRanking().getTotalVerificationCount();

            long contentRankingCount = getContentRanking().getTotalVerificationCount();

            System.out.printf(
                    """
              ===== 동시 생성 결과 =====
              성공 요청 수 = %d
              실패 요청 수 = %d
              VisitVerification = %d
              LocationRanking = %d
              ArtistRanking = %d
              ContentRanking = %d
              ========================
              %n""",
                    successCount.get(),
                    failCount.get(),
                    originalCount,
                    locationRankingCount,
                    artistRankingCount,
                    contentRankingCount);

            /*
             * 우선 모든 방문 인증 요청 자체가 성공해야 함.
             *
             * 여기서 실패한다면
             * 최초 Ranking row 생성 과정의 UNIQUE 충돌 등을 의심.
             */
            assertThat(successCount.get()).isEqualTo(threadCount);

            assertThat(failCount.get()).isZero();

            /*
             * Source of Truth와 Projection이 같아야 함.
             */
            assertThat(originalCount).isEqualTo(threadCount);

            assertThat(locationRankingCount).isEqualTo(originalCount);

            assertThat(artistRankingCount).isEqualTo(originalCount);

            assertThat(contentRankingCount).isEqualTo(originalCount);
        }
    }

    @Nested
    @DisplayName("방문 인증 취소 → 랭킹 감소")
    class CancelVisitVerificationRankingTest {

        @Test
        @DisplayName("방문 인증을 취소하면 관광지/아티스트/콘텐츠 랭킹이 모두 1 감소한다")
        void cancelVisitVerificationReflectsRanking() {

            // given
            /*
             * 두 명 생성
             *
             * 랭킹
             * Location = 2
             * Artist   = 2
             * Content  = 2
             */
            VisitVerificationCreateResult firstResult =
                    visitVerificationCommandService.createVisitVerification(createCommand(1L));

            visitVerificationCommandService.createVisitVerification(createCommand(2L));

            assertRankingCount(2L);

            VisitVerificationCancelCommand cancelCommand =
                    new VisitVerificationCancelCommand(firstResult.visitVerificationId(), 1L);

            // when
            visitVerificationCommandService.cancelVisitVerification(cancelCommand);

            // then
            assertRankingCount(1L);

            /*
             * 원본 VisitVerification도 실제 CANCELED인지 확인
             */
            var canceledVerification =
                    visitVerificationRepository
                            .findById(firstResult.visitVerificationId())
                            .orElseThrow();

            assertThat(canceledVerification.getStatus())
                    .isEqualTo(VisitVerificationStatus.CANCELED);
        }

        @Test
        @DisplayName("여러 방문 인증을 동시에 취소해도 관광지/아티스트/콘텐츠 랭킹이 0으로 정확히 감소한다")
        void concurrentCancelKeepsRankingConsistency() throws InterruptedException {

            // given
            int threadCount = CONCURRENT_REQUEST_COUNT;

            /*
             * 우선 서로 다른 사용자 10명의 방문 인증 생성
             *
             * 여기서는 동시 생성 테스트와 분리하기 위해
             * 순차적으로 생성한다.
             */
            List<VisitVerificationCreateResult> results = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {

                Long concurrentUserId = 1000L + i;

                VisitVerificationCreateResult result =
                        visitVerificationCommandService.createVisitVerification(
                                createCommand(concurrentUserId));

                results.add(result);
            }

            /*
             * 취소 직전에는 정확히 10이어야 함.
             */
            assertRankingCount(threadCount);

            CountDownLatch startLatch = new CountDownLatch(1);

            CountDownLatch endLatch = new CountDownLatch(threadCount);

            AtomicInteger successCount = new AtomicInteger();

            AtomicInteger failCount = new AtomicInteger();

            // when
            try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {

                for (int i = 0; i < threadCount; i++) {

                    int index = i;

                    executorService.submit(
                            () -> {
                                try {
                                    startLatch.await();

                                    Long concurrentUserId = 1000L + index;

                                    VisitVerificationCreateResult result = results.get(index);

                                    VisitVerificationCancelCommand cancelCommand =
                                            new VisitVerificationCancelCommand(
                                                    result.visitVerificationId(), concurrentUserId);

                                    visitVerificationCommandService.cancelVisitVerification(
                                            cancelCommand);

                                    successCount.incrementAndGet();

                                } catch (Throwable throwable) {

                                    failCount.incrementAndGet();

                                    System.err.println(
                                            "방문 인증 취소 실패: "
                                                    + throwable.getClass().getSimpleName()
                                                    + " - "
                                                    + throwable.getMessage());

                                } finally {
                                    endLatch.countDown();
                                }
                            });
                }

                // 동시에 취소
                startLatch.countDown();

                endLatch.await();
            }

            // then
            long locationRankingCount = getLocationRanking().getTotalVerificationCount();

            long artistRankingCount = getArtistRanking().getTotalVerificationCount();

            long contentRankingCount = getContentRanking().getTotalVerificationCount();

            /*
             * 실제 원본 방문 인증도 모두 CANCELED인지 확인
             */
            long canceledCount =
                    results.stream()
                            .map(VisitVerificationCreateResult::visitVerificationId)
                            .map(visitVerificationRepository::findById)
                            .map(Optional::orElseThrow)
                            .filter(
                                    verification ->
                                            verification.getStatus()
                                                    == VisitVerificationStatus.CANCELED)
                            .count();

            System.out.printf(
                    """
              ===== 동시 취소 결과 =====
              성공 요청 수 = %d
              실패 요청 수 = %d
              CANCELED 원본 수 = %d
              LocationRanking = %d
              ArtistRanking = %d
              ContentRanking = %d
              ========================
              %n""",
                    successCount.get(),
                    failCount.get(),
                    canceledCount,
                    locationRankingCount,
                    artistRankingCount,
                    contentRankingCount);

            assertThat(successCount.get()).isEqualTo(threadCount);

            assertThat(failCount.get()).isZero();

            assertThat(canceledCount).isEqualTo(threadCount);

            assertThat(locationRankingCount).isZero();

            assertThat(artistRankingCount).isZero();

            assertThat(contentRankingCount).isZero();
        }
    }
}
