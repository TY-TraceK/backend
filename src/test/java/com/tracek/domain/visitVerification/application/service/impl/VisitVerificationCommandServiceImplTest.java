package com.tracek.domain.visitVerification.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCancelCommand;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCreateCommand;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCreateResult;
import com.tracek.domain.visitVerification.application.service.VisitVerificationCommandService;
import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.exception.VisitVerificationErrorCode;
import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import com.tracek.domain.visitVerification.domain.repository.VisitVerificationRepository;
import com.tracek.global.exception.CustomException;
import java.time.LocalDateTime;
import java.util.List;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class VisitVerificationCommandServiceImplTest {

    @Autowired private VisitVerificationCommandService visitVerificationService;

    @Autowired private VisitVerificationRepository visitVerificationRepository;

    @MockitoBean private EpisodeQueryService episodeQueryService;

    @MockitoBean private LocationQueryService locationQueryService;

    private Long userId;
    private Long locationId;
    private List<Long> artistIds;
    private Long contentId;
    private Double latitude;
    private Double longitude;

    @BeforeEach
    void setUp() {
        userId = 1L;
        locationId = 100L;
        artistIds = List.of(10L, 11L);
        contentId = 20L;
        latitude = 37.5665;
        longitude = 126.9780;

        given(episodeQueryService.isRelatedContentAndArtist(anyLong(), anyLong(), anyLong()))
                .willReturn(true);

        given(locationQueryService.isWithinDistance(anyDouble(), anyDouble(), anyLong(), anyLong()))
                .willReturn(true);
    }

    @AfterEach
    void tearDown() {
        visitVerificationRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("방문 인증 생성 테스트")
    class CreateVisitVerificationTest {

        @Test
        @DisplayName("성공: 복수 아티스트를 포함한 방문 인증이 정상적으로 저장되고 Result가 반환된다.")
        void createVisitVerification_success() {
            // given
            VisitVerificationCreateCommand command =
                    VisitVerificationCreateCommand.builder()
                            .userId(userId)
                            .locationId(locationId)
                            .artistIds(artistIds)
                            .contentId(contentId)
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();

            // when
            VisitVerificationCreateResult result =
                    visitVerificationService.createVisitVerification(command);

            // then
            assertThat(result).isNotNull();

            VisitVerification savedVerification =
                    visitVerificationRepository
                            .findById(result.visitVerificationId())
                            .orElseThrow();

            assertThat(savedVerification.getOwner()).isEqualTo(userId);

            assertThat(savedVerification.getLocationId()).isEqualTo(locationId);

            assertThat(savedVerification.getVerificationTarget().getContentId())
                    .isEqualTo(contentId);
        }

        @Test
        @DisplayName("성공: 장소만 선택해도 방문 인증을 생성할 수 있다.")
        void createVisitVerification_locationOnly_success() {
            VisitVerificationCreateCommand command =
                    VisitVerificationCreateCommand.builder()
                            .userId(userId)
                            .locationId(locationId)
                            .artistIds(List.of())
                            .contentId(null)
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();

            VisitVerificationCreateResult result =
                    visitVerificationService.createVisitVerification(command);

            VisitVerification savedVerification =
                    visitVerificationRepository
                            .findById(result.visitVerificationId())
                            .orElseThrow();

            assertThat(savedVerification.getLocationId()).isEqualTo(locationId);
            assertThat(savedVerification.getVerificationTarget().getContentId()).isNull();
            assertThat(savedVerification.getVerificationTarget().getArtistIds()).isEmpty();
        }

        @Test
        @DisplayName("실패: 이미 방문 인증한 관광지에 다시 방문 인증을 시도하면 ALREADY_VERIFIED 예외가 발생한다.")
        void createVisitVerification_fail_alreadyVerified() {
            // given
            VisitVerificationCreateCommand command =
                    VisitVerificationCreateCommand.builder()
                            .userId(userId)
                            .locationId(locationId)
                            .artistIds(artistIds)
                            .contentId(contentId)
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();

            visitVerificationService.createVisitVerification(command);

            // when & then
            assertThatThrownBy(() -> visitVerificationService.createVisitVerification(command))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(VisitVerificationErrorCode.ALREADY_VERIFIED);
        }

        @Test
        @DisplayName("동시성: 동일 유저가 동시에 같은 관광지에 방문 인증하면 1건만 성공한다.")
        void createVisitVerification_concurrency_twoThreads() throws InterruptedException {

            int threadCount = 2;

            AtomicInteger successCount;
            AtomicInteger failCount;

            try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {

                CountDownLatch startLatch = new CountDownLatch(1);

                CountDownLatch endLatch = new CountDownLatch(threadCount);

                VisitVerificationCreateCommand command =
                        VisitVerificationCreateCommand.builder()
                                .userId(userId)
                                .locationId(locationId)
                                .artistIds(artistIds)
                                .contentId(contentId)
                                .latitude(latitude)
                                .longitude(longitude)
                                .build();

                successCount = new AtomicInteger(0);
                failCount = new AtomicInteger(0);

                for (int i = 0; i < threadCount; i++) {
                    executorService.submit(
                            () -> {
                                try {
                                    startLatch.await();

                                    visitVerificationService.createVisitVerification(command);

                                    successCount.incrementAndGet();

                                } catch (CustomException | DataIntegrityViolationException e) {

                                    failCount.incrementAndGet();

                                } catch (Throwable throwable) {
                                    throwable.printStackTrace();

                                } finally {
                                    endLatch.countDown();
                                }
                            });
                }

                startLatch.countDown();
                endLatch.await();
                executorService.shutdown();
            }

            assertThat(successCount.get()).isEqualTo(1);

            assertThat(failCount.get()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("방문 인증 취소 테스트")
    class CancelVisitVerificationTest {

        @Test
        @DisplayName("성공: 유효한 방문 인증이고 24시간 이내라면 정상적으로 취소된다.")
        void cancelVisitVerification_success() {
            // given
            VisitVerificationCreateCommand createCommand =
                    VisitVerificationCreateCommand.builder()
                            .userId(userId)
                            .locationId(locationId)
                            .artistIds(artistIds)
                            .contentId(contentId)
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();

            VisitVerificationCreateResult createResult =
                    visitVerificationService.createVisitVerification(createCommand);

            VisitVerificationCancelCommand cancelCommand =
                    VisitVerificationCancelCommand.builder()
                            .visitVerificationId(createResult.visitVerificationId())
                            .userId(userId)
                            .build();

            // when
            visitVerificationService.cancelVisitVerification(cancelCommand);

            // then
            VisitVerification visitVerification =
                    visitVerificationRepository
                            .findById(createResult.visitVerificationId())
                            .orElseThrow();

            assertThat(visitVerification.getStatus()).isEqualTo(VisitVerificationStatus.CANCELED);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 방문 인증 ID로 취소하면 NOT_FOUND 예외가 발생한다.")
        void cancelVisitVerification_fail_notFound() {
            // given
            VisitVerificationCancelCommand cancelCommand =
                    VisitVerificationCancelCommand.builder()
                            .visitVerificationId(99999L)
                            .userId(userId)
                            .build();

            // when & then
            assertThatThrownBy(
                            () -> visitVerificationService.cancelVisitVerification(cancelCommand))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(VisitVerificationErrorCode.VISIT_VERIFICATION_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 방문 인증 소유자가 아닌 유저가 취소하면 ACCESS_DINED 예외가 발생한다.")
        void cancelVisitVerification_fail_unauthorized() {
            // given
            VisitVerificationCreateCommand createCommand =
                    VisitVerificationCreateCommand.builder()
                            .userId(userId)
                            .locationId(locationId)
                            .artistIds(artistIds)
                            .contentId(contentId)
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();

            VisitVerificationCreateResult createResult =
                    visitVerificationService.createVisitVerification(createCommand);

            VisitVerificationCancelCommand cancelCommand =
                    VisitVerificationCancelCommand.builder()
                            .visitVerificationId(createResult.visitVerificationId())
                            .userId(99999L)
                            .build();

            // when & then
            assertThatThrownBy(
                            () -> visitVerificationService.cancelVisitVerification(cancelCommand))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(VisitVerificationErrorCode.ACCESS_DINED);
        }

        @Test
        @DisplayName("실패: 생성 후 24시간이 지난 방문 인증은 취소할 수 없다.")
        void cancelVisitVerification_fail_notToday() {
            // given
            VisitVerificationCreateCommand createCommand =
                    VisitVerificationCreateCommand.builder()
                            .userId(userId)
                            .locationId(locationId)
                            .artistIds(artistIds)
                            .contentId(contentId)
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();

            VisitVerificationCreateResult createResult =
                    visitVerificationService.createVisitVerification(createCommand);

            VisitVerification visitVerification =
                    visitVerificationRepository
                            .findById(createResult.visitVerificationId())
                            .orElseThrow();

            ReflectionTestUtils.setField(
                    visitVerification, "verifiedAt", LocalDateTime.now().minusDays(1));

            visitVerificationRepository.save(visitVerification);

            VisitVerificationCancelCommand cancelCommand =
                    VisitVerificationCancelCommand.builder()
                            .visitVerificationId(createResult.visitVerificationId())
                            .userId(userId)
                            .build();

            // when & then
            assertThatThrownBy(
                            () -> visitVerificationService.cancelVisitVerification(cancelCommand))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(VisitVerificationErrorCode.CANNOT_BE_CANCELLED);
        }

        @Test
        @DisplayName("성공: 이미 취소된 방문 인증을 다시 취소해도 예외 없이 종료된다.")
        void cancelVisitVerification_alreadyCancelled_successIgnored() {
            // given
            VisitVerificationCreateCommand createCommand =
                    VisitVerificationCreateCommand.builder()
                            .userId(userId)
                            .locationId(locationId)
                            .artistIds(artistIds)
                            .contentId(contentId)
                            .latitude(latitude)
                            .longitude(longitude)
                            .build();

            VisitVerificationCreateResult createResult =
                    visitVerificationService.createVisitVerification(createCommand);

            VisitVerificationCancelCommand cancelCommand =
                    VisitVerificationCancelCommand.builder()
                            .visitVerificationId(createResult.visitVerificationId())
                            .userId(userId)
                            .build();

            visitVerificationService.cancelVisitVerification(cancelCommand);

            // when & then
            assertThatCode(() -> visitVerificationService.cancelVisitVerification(cancelCommand))
                    .doesNotThrowAnyException();

            VisitVerification visitVerification =
                    visitVerificationRepository
                            .findById(createResult.visitVerificationId())
                            .orElseThrow();

            assertThat(visitVerification.getStatus()).isEqualTo(VisitVerificationStatus.CANCELED);
        }
    }
}
