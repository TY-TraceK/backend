package com.tracek.domain.ranking.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.ranking.domain.model.ArtistLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.ContentArtistLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.ContentArtistVisitRanking;
import com.tracek.domain.ranking.domain.model.ContentLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.LocationVisitRanking;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.ArtistLocationVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.ContentArtistLocationVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.ContentArtistVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.ContentLocationVisitRankingJpaRepository;
import com.tracek.domain.ranking.infrastructure.persistence.jpa.LocationVisitRankingJpaRepository;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCancelCommand;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCreateCommand;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationUpdateCommand;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCreateResult;
import com.tracek.domain.visitVerification.application.service.VisitVerificationCommandService;
import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.infrastructure.persistence.VisitVerificationJpaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

    @Autowired private LocationVisitRankingJpaRepository locationVisitRankingRepository;

    @Autowired private ArtistLocationVisitRankingJpaRepository artistLocationVisitRankingRepository;

    @Autowired
    private ContentLocationVisitRankingJpaRepository contentLocationVisitRankingRepository;

    @Autowired private ContentArtistVisitRankingJpaRepository contentArtistVisitRankingRepository;

    @Autowired
    private ContentArtistLocationVisitRankingJpaRepository
            contentArtistLocationVisitRankingRepository;

    @MockitoBean private LocationQueryService locationQueryService;

    @MockitoBean private EpisodeQueryService episodeQueryService;

    private Long userId;
    private Long locationId;
    private Long artistId;
    private Long contentId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        locationId = 100L;
        artistId = 10L;
        contentId = 20L;

        given(episodeQueryService.isRelatedContentAndArtist(anyLong(), anyLong(), anyLong()))
                .willReturn(true);
        given(episodeQueryService.isRelatedContent(anyLong(), anyLong())).willReturn(true);
        given(locationQueryService.isWithinDistance(anyDouble(), anyDouble(), anyLong(), anyLong()))
                .willReturn(true);

        initializeRankingRows();
    }

    /** 랭킹 후보는 사전에 생성되어 있으며 방문 인증이 없는 상태에서는 count = 0으로 존재한다. */
    private void initializeRankingRows() {
        locationVisitRankingRepository.save(LocationVisitRanking.create(locationId));

        artistLocationVisitRankingRepository.save(
                ArtistLocationVisitRanking.create(locationId, artistId));

        contentLocationVisitRankingRepository.save(
                ContentLocationVisitRanking.create(locationId, contentId));
    }

    @AfterEach
    void tearDown() {
        contentArtistLocationVisitRankingRepository.deleteAllInBatch();
        contentArtistVisitRankingRepository.deleteAllInBatch();
        contentLocationVisitRankingRepository.deleteAllInBatch();
        artistLocationVisitRankingRepository.deleteAllInBatch();
        locationVisitRankingRepository.deleteAllInBatch();
        visitVerificationRepository.deleteAllInBatch();
    }

    private VisitVerificationCreateCommand createCommand(Long userId) {
        return VisitVerificationCreateCommand.builder()
                .userId(userId)
                .locationId(locationId)
                .contentId(contentId)
                .artistId(artistId)
                .latitude(LATITUDE)
                .longitude(LONGITUDE)
                .build();
    }

    private long getLocationRankingCount() {
        return locationVisitRankingRepository
                .findByLocationId(locationId)
                .map(LocationVisitRanking::getTotalVerificationCount)
                .orElseThrow();
    }

    private long getArtistRankingCount() {
        return artistLocationVisitRankingRepository
                .findByLocationIdAndArtistId(locationId, artistId)
                .map(ArtistLocationVisitRanking::getTotalVerificationCount)
                .orElseThrow();
    }

    private long getContentRankingCount() {
        return contentLocationVisitRankingRepository
                .findByLocationIdAndContentId(locationId, contentId)
                .map(ContentLocationVisitRanking::getTotalVerificationCount)
                .orElseThrow();
    }

    private long getArtistRankingCount(Long targetArtistId) {
        return artistLocationVisitRankingRepository
                .findByLocationIdAndArtistId(locationId, targetArtistId)
                .map(ArtistLocationVisitRanking::getTotalVerificationCount)
                .orElseThrow();
    }

    private long getContentRankingCount(Long targetContentId) {
        return contentLocationVisitRankingRepository
                .findByLocationIdAndContentId(locationId, targetContentId)
                .map(ContentLocationVisitRanking::getTotalVerificationCount)
                .orElseThrow();
    }

    private long getContentArtistRankingCount(Long targetContentId, Long targetArtistId) {

        return contentArtistVisitRankingRepository.findAll().stream()
                .filter(
                        ranking ->
                                Objects.equals(ranking.getContentId(), targetContentId)
                                        && Objects.equals(ranking.getArtistId(), targetArtistId))
                .findFirst()
                .map(ContentArtistVisitRanking::getTotalVerificationCount)
                .orElseThrow();
    }

    private long getContentArtistLocationRankingCount(Long targetContentId, Long targetArtistId) {

        return contentArtistLocationVisitRankingRepository.findAll().stream()
                .filter(
                        ranking ->
                                Objects.equals(ranking.getLocationId(), locationId)
                                        && Objects.equals(ranking.getContentId(), targetContentId)
                                        && Objects.equals(ranking.getArtistId(), targetArtistId))
                .findFirst()
                .map(ContentArtistLocationVisitRanking::getTotalVerificationCount)
                .orElseThrow();
    }

    private void assertRankingCount(long expectedCount) {
        assertThat(getLocationRankingCount()).isEqualTo(expectedCount);
        assertThat(getArtistRankingCount()).isEqualTo(expectedCount);
        assertThat(getContentRankingCount()).isEqualTo(expectedCount);
    }

    /** 랭킹 row 자체는 항상 한 개만 존재해야 한다. */
    private void assertRankingRowCountIsOne() {
        assertThat(locationVisitRankingRepository.count()).isEqualTo(1L);
        assertThat(artistLocationVisitRankingRepository.count()).isEqualTo(1L);
        assertThat(contentLocationVisitRankingRepository.count()).isEqualTo(1L);
    }

    private void printAllTablesStatus() {
        System.out.println("\n================ [ 현재 DB 테이블 상태 출력 ] ================");

        System.out.println("--- VisitVerification 테이블 목록 ---");

        visitVerificationRepository
                .findAll()
                .forEach(
                        verification ->
                                System.out.println(
                                        "ID: "
                                                + verification.getId()
                                                + ", UserId: "
                                                + verification.getOwner()
                                                + ", LocationId: "
                                                + verification.getLocationId()
                                                + ", Status: "
                                                + verification.getStatus()));

        System.out.println("--- LocationVisitRanking 테이블 목록 ---");

        locationVisitRankingRepository
                .findAll()
                .forEach(
                        ranking ->
                                System.out.println(
                                        "ID: "
                                                + ranking.getId()
                                                + ", LocationId: "
                                                + ranking.getLocationId()
                                                + ", Count: "
                                                + ranking.getTotalVerificationCount()));

        System.out.println("--- ArtistLocationVisitRanking 테이블 목록 ---");

        artistLocationVisitRankingRepository
                .findAll()
                .forEach(
                        ranking ->
                                System.out.println(
                                        "ID: "
                                                + ranking.getId()
                                                + ", LocationId: "
                                                + ranking.getLocationId()
                                                + ", ArtistId: "
                                                + ranking.getArtistId()
                                                + ", Count: "
                                                + ranking.getTotalVerificationCount()));

        System.out.println("--- ContentLocationVisitRanking 테이블 목록 ---");

        contentLocationVisitRankingRepository
                .findAll()
                .forEach(
                        ranking ->
                                System.out.println(
                                        "ID: "
                                                + ranking.getId()
                                                + ", LocationId: "
                                                + ranking.getLocationId()
                                                + ", ContentId: "
                                                + ranking.getContentId()
                                                + ", Count: "
                                                + ranking.getTotalVerificationCount()));

        System.out.println("=========================================================\n");
    }

    @Nested
    @DisplayName("방문 인증 생성 → 랭킹 증가")
    class CreateVisitVerificationRankingTest {

        @Test
        @DisplayName("기존 랭킹 row가 존재할 때 방문 인증 1건이 생성되면 관광지/아티스트/콘텐츠 랭킹이 모두 1 증가한다")
        void createVisitVerificationReflectsRanking() {

            // given
            assertRankingCount(0L);
            assertRankingRowCountIsOne();

            VisitVerificationCreateCommand command = createCommand(userId);

            // when
            long startTime = System.currentTimeMillis();

            visitVerificationCommandService.createVisitVerification(command);

            long endTime = System.currentTimeMillis();

            // then
            System.out.println("단건 생성 실행 시간: " + (endTime - startTime) + "ms");

            printAllTablesStatus();

            long originalCount = visitVerificationRepository.countByLocationId(locationId);

            assertThat(originalCount).isEqualTo(1L);

            assertRankingCount(1L);

            /*
             * 새로운 랭킹 row가 생성된 것이 아니라
             * 기존 count=0인 row가 수정되었는지 확인
             */
            assertRankingRowCountIsOne();
        }

        @Test
        @DisplayName("기존 랭킹 row에 여러 사용자가 동시에 방문 인증해도 원본 수와 랭킹 집계 수가 일치한다")
        void concurrentCreateKeepsRankingConsistency() throws InterruptedException {

            // given
            assertRankingCount(0L);
            assertRankingRowCountIsOne();

            int threadCount = CONCURRENT_REQUEST_COUNT;

            CountDownLatch startLatch = new CountDownLatch(1);

            CountDownLatch endLatch = new CountDownLatch(threadCount);

            AtomicInteger successCount = new AtomicInteger();

            AtomicInteger failCount = new AtomicInteger();

            long startTime = System.currentTimeMillis();

            // when
            try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {

                for (int i = 0; i < threadCount; i++) {

                    Long concurrentUserId = 100L + i;

                    final int threadIdx = i + 1;

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
                                            "[스레드 "
                                                    + threadIdx
                                                    + "] 방문 인증 생성 실패: "
                                                    + throwable.getClass().getSimpleName()
                                                    + " - "
                                                    + throwable.getMessage());

                                } finally {

                                    endLatch.countDown();
                                }
                            });
                }

                startLatch.countDown();

                endLatch.await();
            }

            long endTime = System.currentTimeMillis();

            // then
            printAllTablesStatus();

            long originalCount = visitVerificationRepository.countByLocationId(locationId);

            long locationRankingCount = getLocationRankingCount();

            long artistRankingCount = getArtistRankingCount();

            long contentRankingCount = getContentRankingCount();

            System.out.printf(
                    """
              ===== 동시 생성 결과 =====
              실행 소요 시간 = %d ms
              성공 요청 수 = %d
              실패 요청 수 = %d
              VisitVerification = %d
              LocationRanking = %d
              ArtistRanking = %d
              ContentRanking = %d
              ========================
              %n""",
                    (endTime - startTime),
                    successCount.get(),
                    failCount.get(),
                    originalCount,
                    locationRankingCount,
                    artistRankingCount,
                    contentRankingCount);

            assertThat(successCount.get()).isEqualTo(threadCount);

            assertThat(failCount.get()).isZero();

            assertThat(originalCount).isEqualTo(threadCount);

            /*
             * 핵심 동시성 검증
             *
             * 10개의 방문 인증이 성공했다면
             * ranking count도 정확히 10이어야 한다.
             */
            assertThat(locationRankingCount).isEqualTo(originalCount);

            assertThat(artistRankingCount).isEqualTo(originalCount);

            assertThat(contentRankingCount).isEqualTo(originalCount);

            /*
             * 동시 요청으로 인해
             * 랭킹 row가 추가로 생성되지 않았는지 확인
             */
            assertRankingRowCountIsOne();
        }
    }

    @Nested
    @DisplayName("방문 인증 취소 → 랭킹 감소")
    class CancelVisitVerificationRankingTest {

        @Test
        @DisplayName("방문 인증을 취소하면 기존 관광지/아티스트/콘텐츠 랭킹이 모두 1 감소한다")
        void cancelVisitVerificationReflectsRanking() {

            // given
            assertRankingCount(0L);
            assertRankingRowCountIsOne();

            VisitVerificationCreateResult firstResult =
                    visitVerificationCommandService.createVisitVerification(createCommand(1L));

            visitVerificationCommandService.createVisitVerification(createCommand(2L));

            assertRankingCount(2L);

            VisitVerificationCancelCommand cancelCommand =
                    new VisitVerificationCancelCommand(firstResult.visitVerificationId(), 1L);

            // when
            long startTime = System.currentTimeMillis();

            visitVerificationCommandService.cancelVisitVerification(cancelCommand);

            long endTime = System.currentTimeMillis();

            // then
            System.out.println("단건 취소 실행 시간: " + (endTime - startTime) + "ms");

            printAllTablesStatus();

            assertRankingCount(1L);

            assertRankingRowCountIsOne();

            var canceledVerification =
                    visitVerificationRepository
                            .findById(firstResult.visitVerificationId())
                            .orElseThrow();

            assertThat(canceledVerification.getStatus())
                    .isEqualTo(VisitVerificationStatus.CANCELED);
        }

        @Test
        @DisplayName("기존 랭킹 row에서 여러 방문 인증을 동시에 취소해도 랭킹이 0으로 정확히 감소한다")
        void concurrentCancelKeepsRankingConsistency() throws InterruptedException {

            // given
            assertRankingCount(0L);
            assertRankingRowCountIsOne();

            int threadCount = CONCURRENT_REQUEST_COUNT;

            List<VisitVerificationCreateResult> results = new ArrayList<>();

            /*
             * 먼저 방문 인증 10건 생성
             *
             * 시작 랭킹
             * 0 → 10
             */
            for (int i = 0; i < threadCount; i++) {

                Long concurrentUserId = 1000L + i;

                VisitVerificationCreateResult result =
                        visitVerificationCommandService.createVisitVerification(
                                createCommand(concurrentUserId));

                results.add(result);
            }

            assertRankingCount(threadCount);

            CountDownLatch startLatch = new CountDownLatch(1);

            CountDownLatch endLatch = new CountDownLatch(threadCount);

            AtomicInteger successCount = new AtomicInteger();

            AtomicInteger failCount = new AtomicInteger();

            long startTime = System.currentTimeMillis();

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

                startLatch.countDown();

                endLatch.await();
            }

            long endTime = System.currentTimeMillis();

            // then
            printAllTablesStatus();

            long locationRankingCount = getLocationRankingCount();

            long artistRankingCount = getArtistRankingCount();

            long contentRankingCount = getContentRankingCount();

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
              실행 소요 시간 = %d ms
              성공 요청 수 = %d
              실패 요청 수 = %d
              CANCELED 원본 수 = %d
              LocationRanking = %d
              ArtistRanking = %d
              ContentRanking = %d
              ========================
              %n""",
                    (endTime - startTime),
                    successCount.get(),
                    failCount.get(),
                    canceledCount,
                    locationRankingCount,
                    artistRankingCount,
                    contentRankingCount);

            assertThat(successCount.get()).isEqualTo(threadCount);

            assertThat(failCount.get()).isZero();

            assertThat(canceledCount).isEqualTo(threadCount);

            /*
             * 10 → 0이 정확히 되어야 한다.
             */
            assertThat(locationRankingCount).isZero();

            assertThat(artistRankingCount).isZero();

            assertThat(contentRankingCount).isZero();

            /*
             * count가 0이 되어도 랭킹 후보 row 자체는 유지한다.
             */
            assertRankingRowCountIsOne();
        }
    }

    @Nested
    @DisplayName("방문 인증 수정 → 랭킹 변경")
    class UpdateVisitVerificationRankingTest {

        private Long updatedArtistId;
        private Long updatedContentId;

        @BeforeEach
        void setUpUpdateRankingRows() {
            updatedArtistId = 11L;
            updatedContentId = 21L;

            /*
             * 수정 후 이동할 랭킹 후보 row를 미리 생성한다.
             */
            artistLocationVisitRankingRepository.save(
                    ArtistLocationVisitRanking.create(locationId, updatedArtistId));

            contentLocationVisitRankingRepository.save(
                    ContentLocationVisitRanking.create(locationId, updatedContentId));
        }

        private VisitVerificationUpdateCommand updateCommand(
                Long visitVerificationId, Long targetContentId, Long targetArtistId) {

            return VisitVerificationUpdateCommand.builder()
                    .visitVerificationId(visitVerificationId)
                    .userId(userId)
                    .contentId(targetContentId)
                    .artistId(targetArtistId)
                    .build();
        }

        @Test
        @DisplayName("콘텐츠와 아티스트를 모두 수정하면 기존 랭킹은 감소하고 새로운 랭킹은 증가한다")
        void updateContentAndArtistReflectsRanking() {

            // given
            VisitVerificationCreateResult result =
                    visitVerificationCommandService.createVisitVerification(createCommand(userId));

            assertThat(getLocationRankingCount()).isEqualTo(1L);
            assertThat(getArtistRankingCount(artistId)).isEqualTo(1L);
            assertThat(getContentRankingCount(contentId)).isEqualTo(1L);

            assertThat(getArtistRankingCount(updatedArtistId)).isZero();
            assertThat(getContentRankingCount(updatedContentId)).isZero();

            VisitVerificationUpdateCommand command =
                    updateCommand(result.visitVerificationId(), updatedContentId, updatedArtistId);

            // when
            long startTime = System.currentTimeMillis();

            visitVerificationCommandService.updateVisitVerification(command);

            long endTime = System.currentTimeMillis();

            // then
            System.out.println("단건 수정 실행 시간: " + (endTime - startTime) + "ms");

            printAllTablesStatus();

            /*
             * location은 수정되지 않으므로 그대로 유지
             */
            assertThat(getLocationRankingCount()).isEqualTo(1L);

            /*
             * artist ranking
             *
             * 기존 artist
             * 1 → 0
             *
             * 변경 artist
             * 0 → 1
             */
            assertThat(getArtistRankingCount(artistId)).isZero();
            assertThat(getArtistRankingCount(updatedArtistId)).isEqualTo(1L);

            /*
             * content ranking
             *
             * 기존 content
             * 1 → 0
             *
             * 변경 content
             * 0 → 1
             */
            assertThat(getContentRankingCount(contentId)).isZero();
            assertThat(getContentRankingCount(updatedContentId)).isEqualTo(1L);
        }

        @Test
        @DisplayName("콘텐츠만 수정하면 콘텐츠 랭킹만 이동하고 관광지/아티스트 랭킹은 유지된다")
        void updateOnlyContentReflectsRanking() {

            // given
            VisitVerificationCreateResult result =
                    visitVerificationCommandService.createVisitVerification(createCommand(userId));

            assertThat(getLocationRankingCount()).isEqualTo(1L);
            assertThat(getArtistRankingCount(artistId)).isEqualTo(1L);
            assertThat(getContentRankingCount(contentId)).isEqualTo(1L);
            assertThat(getContentRankingCount(updatedContentId)).isZero();

            VisitVerificationUpdateCommand command =
                    updateCommand(result.visitVerificationId(), updatedContentId, artistId);

            // when
            visitVerificationCommandService.updateVisitVerification(command);

            // then

            /*
             * location 변경 없음
             */
            assertThat(getLocationRankingCount()).isEqualTo(1L);

            /*
             * artist 변경 없음
             */
            assertThat(getArtistRankingCount(artistId)).isEqualTo(1L);
            assertThat(getArtistRankingCount(updatedArtistId)).isZero();

            /*
             * content만 이동
             */
            assertThat(getContentRankingCount(contentId)).isZero();
            assertThat(getContentRankingCount(updatedContentId)).isEqualTo(1L);
        }

        @Test
        @DisplayName("아티스트만 수정하면 아티스트 랭킹만 이동하고 관광지/콘텐츠 랭킹은 유지된다")
        void updateOnlyArtistReflectsRanking() {

            // given
            VisitVerificationCreateResult result =
                    visitVerificationCommandService.createVisitVerification(createCommand(userId));

            assertThat(getLocationRankingCount()).isEqualTo(1L);
            assertThat(getArtistRankingCount(artistId)).isEqualTo(1L);
            assertThat(getArtistRankingCount(updatedArtistId)).isZero();
            assertThat(getContentRankingCount(contentId)).isEqualTo(1L);

            VisitVerificationUpdateCommand command =
                    updateCommand(result.visitVerificationId(), contentId, updatedArtistId);

            // when
            visitVerificationCommandService.updateVisitVerification(command);

            // then

            /*
             * location 변경 없음
             */
            assertThat(getLocationRankingCount()).isEqualTo(1L);

            /*
             * content 변경 없음
             */
            assertThat(getContentRankingCount(contentId)).isEqualTo(1L);
            assertThat(getContentRankingCount(updatedContentId)).isZero();

            /*
             * artist만 이동
             */
            assertThat(getArtistRankingCount(artistId)).isZero();
            assertThat(getArtistRankingCount(updatedArtistId)).isEqualTo(1L);
        }

        @Test
        @DisplayName("콘텐츠와 아티스트가 동일하면 기존 랭킹은 변경되지 않는다")
        void updateSameTargetDoesNotChangeRanking() {

            // given
            VisitVerificationCreateResult result =
                    visitVerificationCommandService.createVisitVerification(createCommand(userId));

            assertThat(getLocationRankingCount()).isEqualTo(1L);
            assertThat(getArtistRankingCount(artistId)).isEqualTo(1L);
            assertThat(getContentRankingCount(contentId)).isEqualTo(1L);

            VisitVerificationUpdateCommand command =
                    updateCommand(result.visitVerificationId(), contentId, artistId);

            // when
            visitVerificationCommandService.updateVisitVerification(command);

            // then
            assertThat(getLocationRankingCount()).isEqualTo(1L);
            assertThat(getArtistRankingCount(artistId)).isEqualTo(1L);
            assertThat(getContentRankingCount(contentId)).isEqualTo(1L);
        }
    }
}
