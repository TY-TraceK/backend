package com.tracek.domain.visitVerification.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCancelCommand;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCreateCommand;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCreateResult;
import com.tracek.domain.visitVerification.application.service.VisitVerificationCommandService;
import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.exception.VisitVerificationErrorCode;
import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import com.tracek.domain.visitVerification.domain.repository.VisitVerificationRepository;
import com.tracek.global.exception.CustomException;
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

    @MockitoBean
    private com.tracek.domain.content.application.service.EpisodeQueryService episodeQueryService;

    @MockitoBean
    private com.tracek.domain.location.application.service.LocationQueryService
            locationQueryService;

    private Long userId;
    private Long locationId;
    private Long artistId;
    private Long contentId;
    private Double latitude;
    private Double longitude;

    @BeforeEach
    void setUp() {
        userId = 1L;
        locationId = 100L;
        artistId = 10L;
        contentId = 20L;
        latitude = 37.5665;
        longitude = 126.9780;

        // 1. 연관 관계가 존재함(true)으로 설정하여 서비스의 !isRelatedVerifiedTarget 통과
        given(episodeQueryService.isRelatedContentAndArtist(anyLong(), anyLong(), anyLong()))
                .willReturn(true);
        given(episodeQueryService.isRelatedContent(anyLong(), anyLong())).willReturn(true);

        // 2. 반경 내에 있음(true)으로 설정하여 서비스의 isWithinDistance 통과
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
        @DisplayName("성공: 방문 인증 생성 요청 시 정상적으로 방문 인증가 저장되고 Result가 반환된다.")
        void createVisitVerification_success() {
            // given
            VisitVerificationCreateCommand command =
                    new VisitVerificationCreateCommand(
                            userId, locationId, artistId, contentId, latitude, longitude);

            // when
            VisitVerificationCreateResult result =
                    visitVerificationService.createVisitVerification(command);

            // then
            assertThat(result).isNotNull();

            List<VisitVerification> visitVerifications = visitVerificationRepository.findAll();
            assertThat(visitVerifications).hasSize(1);
            VisitVerification savedVerification = visitVerifications.getFirst();
            assertThat(savedVerification.getOwner()).isEqualTo(userId);
            assertThat(savedVerification.getLocationId()).isEqualTo(locationId);
        }

        @Test
        @DisplayName("실패: 이미 방문 인증한 관광지에 다시 방문 인증를 시도하면 이미 방문 인증했다는 에러가 발생한다.")
        void createVisitVerification_fail_alreadyVerified() {
            // given: 1차 방문 인증 진행
            VisitVerificationCreateCommand command =
                    new VisitVerificationCreateCommand(
                            userId, locationId, artistId, contentId, latitude, longitude);
            visitVerificationService.createVisitVerification(command);

            // when & then: 동일 커맨드로 2차 방문 인증 시도시 예외 발생
            assertThatThrownBy(() -> visitVerificationService.createVisitVerification(command))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(VisitVerificationErrorCode.ALREADY_VERIFIED);
        }

        @Test
        @DisplayName("동시성: 동일한 유저가 동시에 2개의 스레드로 방문 인증를 요청하면 1건만 성공하고 1건은 실패한다.")
        void createVisitVerification_concurrency_twoThreads() throws InterruptedException {
            int threadCount = 2;
            AtomicInteger successCount;
            AtomicInteger failCount;
            try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
                CountDownLatch startLatch = new CountDownLatch(1);
                CountDownLatch endLatch = new CountDownLatch(threadCount);

                VisitVerificationCreateCommand command =
                        new VisitVerificationCreateCommand(
                                userId, locationId, artistId, contentId, latitude, longitude);

                successCount = new AtomicInteger(0);
                failCount = new AtomicInteger(0);

                for (int i = 0; i < threadCount; i++) {
                    final int threadIndex = i;
                    executorService.submit(
                            () -> {
                                try {
                                    startLatch.await();
                                    visitVerificationService.createVisitVerification(command);
                                    successCount.incrementAndGet();
                                } catch (CustomException | DataIntegrityViolationException e) {
                                    failCount.incrementAndGet();
                                } catch (Throwable t) {
                                    t.printStackTrace();
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
        @DisplayName("성공: 유효한 방문 인증이고 당일 생성된 방문 인증라면 정상적으로 취소(CANCELED)된다.")
        void cancelVisitVerification_success() {
            // given: 방문 인증 생성
            VisitVerificationCreateCommand createCommand =
                    new VisitVerificationCreateCommand(
                            userId, locationId, artistId, contentId, latitude, longitude);
            VisitVerificationCreateResult createResult =
                    visitVerificationService.createVisitVerification(createCommand);

            VisitVerificationCancelCommand cancelCommand =
                    new VisitVerificationCancelCommand(createResult.visitVerificationId(), userId);

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
        @DisplayName("실패: 존재하지 않는 방문 인증 ID로 취소를 요청하면 NOT_FOUND 예외가 발생한다.")
        void cancelVisitVerification_fail_notFound() {
            // given
            VisitVerificationCancelCommand cancelCommand =
                    new VisitVerificationCancelCommand(99999L, userId);

            // when & then
            assertThatThrownBy(
                            () -> visitVerificationService.cancelVisitVerification(cancelCommand))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(VisitVerificationErrorCode.VISIT_VERIFICATION_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 방문 인증 소유자가 아닌 유저가 취소를 요청하면 ACCESS_DINED 예외가 발생한다.")
        void cancelVisitVerification_fail_unauthorized() {
            // given: 유저 1이 방문 인증 생성
            VisitVerificationCreateCommand createCommand =
                    new VisitVerificationCreateCommand(
                            userId, locationId, artistId, contentId, latitude, longitude);
            VisitVerificationCreateResult createResult =
                    visitVerificationService.createVisitVerification(createCommand);

            VisitVerificationCancelCommand cancelCommand =
                    new VisitVerificationCancelCommand(createResult.visitVerificationId(), 99999L);

            // when & then
            assertThatThrownBy(
                            () -> visitVerificationService.cancelVisitVerification(cancelCommand))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(VisitVerificationErrorCode.ACCESS_DINED);
        }

        @Test
        @DisplayName("실패: 당일 생성된 방문 인증가 아니라면 취소할 수 없고 CANNOT_BE_CANCELLED 예외가 발생한다.")
        void cancelVisitVerification_fail_notToday() {
            // given: 방문 인증 생성 후, 리플렉션을 이용해 강제로 날짜를 어제로 조작
            VisitVerificationCreateCommand createCommand =
                    new VisitVerificationCreateCommand(
                            userId, locationId, artistId, contentId, latitude, longitude);
            VisitVerificationCreateResult createResult =
                    visitVerificationService.createVisitVerification(createCommand);

            VisitVerification visitVerification =
                    visitVerificationRepository
                            .findById(createResult.visitVerificationId())
                            .orElseThrow();

            ReflectionTestUtils.setField(
                    visitVerification, "verifiedAt", java.time.LocalDateTime.now().minusDays(1));
            visitVerificationRepository.save(visitVerification);

            VisitVerificationCancelCommand cancelCommand =
                    new VisitVerificationCancelCommand(createResult.visitVerificationId(), userId);

            // when & then
            assertThatThrownBy(
                            () -> visitVerificationService.cancelVisitVerification(cancelCommand))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(VisitVerificationErrorCode.CANNOT_BE_CANCELLED);
        }

        @Test
        @DisplayName("성공: 이미 취소된 방문 인증에 대해 다시 취소 요청을 보내도 예외 없이 정상 종료된다.")
        void cancelVisitVerification_alreadyCancelled_successIgnored() {
            // given: 방문 인증 생성
            VisitVerificationCreateCommand createCommand =
                    new VisitVerificationCreateCommand(
                            userId, locationId, artistId, contentId, latitude, longitude);
            VisitVerificationCreateResult createResult =
                    visitVerificationService.createVisitVerification(createCommand);

            VisitVerificationCancelCommand cancelCommand =
                    new VisitVerificationCancelCommand(createResult.visitVerificationId(), userId);

            // 1차 취소
            visitVerificationService.cancelVisitVerification(cancelCommand);

            // when & then: 2차 취소 요청 시 예외가 발생하지 않고 그대로 통과되는지 확인
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
