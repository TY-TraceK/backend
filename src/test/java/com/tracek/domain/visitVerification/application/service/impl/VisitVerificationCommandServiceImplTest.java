package com.tracek.domain.visitVerification.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.location.application.dto.LocationContentArtistResult;
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

    @MockitoBean private LocationQueryService locationQueryService;

    private Long userId;
    private Long locationId;
    private Long locationContentArtistId;
    private String snapshotName;

    @BeforeEach
    void setUp() {
        userId = 1L;
        locationId = 100L;
        locationContentArtistId = 1000L;
        snapshotName = "경복궁 | BTS | Run BTS Ep.100";

        // 1. Mock DTO 생성 및 Getter 설정
        LocationContentArtistResult mockResult =
                org.mockito.Mockito.mock(LocationContentArtistResult.class);
        given(mockResult.getLocationId()).willReturn(locationId);
        given(mockResult.getArtistId()).willReturn(10L);
        given(mockResult.getContentId()).willReturn(20L);

        // 2. 어떤 Long 값이 들어오더라도 mockResult를 반환하도록 설정
        given(locationQueryService.getMappingById(org.mockito.ArgumentMatchers.anyLong()))
                .willReturn(mockResult);
    }

    @AfterEach
    void tearDown() {
        visitVerificationRepository.deleteAllInBatch();
    }

    @Nested
    @DisplayName("투표 생성 테스트")
    class CreateVisitVerificationTest {

        @Test
        @DisplayName("성공: 투표 생성 요청 시 정상적으로 투표가 저장되고 Result가 반환된다.")
        void createVisitVerification_success() {
            // given
            VisitVerificationCreateCommand command =
                    new VisitVerificationCreateCommand(
                            userId, locationId, locationContentArtistId, snapshotName);

            // when
            VisitVerificationCreateResult result =
                    visitVerificationService.createvisitVerification(command);

            // then
            assertThat(result).isNotNull();

            List<VisitVerification> visitVerifications = visitVerificationRepository.findAll();
            assertThat(visitVerifications).hasSize(1);
            VisitVerification savedvisitVerification = visitVerifications.getFirst();
            assertThat(savedvisitVerification.getOwner()).isEqualTo(userId);
            assertThat(savedvisitVerification.getVerificationTarget().getLocationId())
                    .isEqualTo(locationId);
        }

        @Test
        @DisplayName("실패: 이미 투표한 관광지에 다시 투표를 시도하면 이미 투표했다는 에러가 발생한다.")
        void createVisitVerification_fail_alreadyvisitVerification() {
            // given: 1차 투표 진행
            VisitVerificationCreateCommand command =
                    new VisitVerificationCreateCommand(
                            userId, locationId, locationContentArtistId, snapshotName);
            visitVerificationService.createvisitVerification(command);

            // when & then: 동일 커맨드로 2차 투표 시도시 예외 발생
            assertThatThrownBy(() -> visitVerificationService.createvisitVerification(command))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(VisitVerificationErrorCode.ALREADY_VERIFIED);
        }

        @Test
        @DisplayName("동시성: 동일한 유저가 동시에 2개의 스레드로 투표를 요청하면 1건만 성공하고 1건은 실패한다.")
        void createVisitVerification_concurrency_twoThreads() throws InterruptedException {
            int threadCount = 2;
            AtomicInteger successCount;
            AtomicInteger failCount;
            try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
                CountDownLatch startLatch = new CountDownLatch(1);
                CountDownLatch endLatch = new CountDownLatch(threadCount);

                VisitVerificationCreateCommand command =
                        new VisitVerificationCreateCommand(
                                userId, locationId, locationContentArtistId, snapshotName);

                successCount = new AtomicInteger(0);
                failCount = new AtomicInteger(0);

                for (int i = 0; i < threadCount; i++) {
                    final int threadIndex = i;
                    executorService.submit(
                            () -> {
                                try {
                                    startLatch.await();
                                    visitVerificationService.createvisitVerification(command);
                                    System.out.println(">>> [스레드 " + threadIndex + "] 성공!");
                                    successCount.incrementAndGet();
                                } catch (CustomException e) {
                                    System.out.println(
                                            ">>> [스레드 "
                                                    + threadIndex
                                                    + "] CustomException 발생: "
                                                    + e.getErrorCode());
                                    failCount.incrementAndGet();
                                } catch (DataIntegrityViolationException e) {
                                    System.out.println(
                                            ">>> [스레드 " + threadIndex + "] DB 유니크 충돌 발생!");
                                    failCount.incrementAndGet();
                                } catch (Throwable t) {
                                    System.err.println(
                                            ">>> [스레드 "
                                                    + threadIndex
                                                    + "] 예상치 못한 치명적 예외: "
                                                    + t.getClass().getName()
                                                    + " - "
                                                    + t.getMessage());
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

            System.out.println(
                    "최종 successCount = " + successCount.get() + ", failCount = " + failCount.get());

            assertThat(successCount.get()).isEqualTo(1);
            assertThat(failCount.get()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("투표 취소 테스트")
    class CancelVisitVerificationTest {

        @Test
        @DisplayName("성공: 유효한 투표이고 당일 생성된 투표라면 정상적으로 취소(CANCELED)된다.")
        void cancelVisitVerification_success() {
            // given: 투표 생성
            VisitVerificationCreateCommand createCommand =
                    new VisitVerificationCreateCommand(
                            userId, locationId, locationContentArtistId, snapshotName);
            VisitVerificationCreateResult createResult =
                    visitVerificationService.createvisitVerification(createCommand);

            VisitVerificationCancelCommand cancelCommand =
                    new VisitVerificationCancelCommand(createResult.visitVerificationId(), userId);

            // when
            visitVerificationService.cancelvisitVerification(cancelCommand);

            // then
            VisitVerification visitVerification =
                    visitVerificationRepository
                            .findById(createResult.visitVerificationId())
                            .orElseThrow();
            assertThat(visitVerification.getStatus()).isEqualTo(VisitVerificationStatus.CANCELED);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 투표 ID로 취소를 요청하면 visitVerification_NOT_FOUND 예외가 발생한다.")
        void cancelVisitVerification_fail_notFound() {
            // given
            VisitVerificationCancelCommand cancelCommand =
                    new VisitVerificationCancelCommand(
                            99999L, userId // 존재하지 않는 ID
                            );

            // when & then
            assertThatThrownBy(
                            () -> visitVerificationService.cancelvisitVerification(cancelCommand))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(VisitVerificationErrorCode.VISIT_VERIFICATION_NOT_FOUND);
        }

        @Test
        @DisplayName("실패: 투표 소유자가 아닌 유저가 취소를 요청하면 UNAUTHORIZED_visitVerification_ACCESS 예외가 발생한다.")
        void cancel_visitVerification_fail_unauthorized() {
            // given: 유저 1이 투표 생성
            VisitVerificationCreateCommand createCommand =
                    new VisitVerificationCreateCommand(
                            userId, locationId, locationContentArtistId, snapshotName);
            VisitVerificationCreateResult createResult =
                    visitVerificationService.createvisitVerification(createCommand);

            VisitVerificationCancelCommand cancelCommand =
                    new VisitVerificationCancelCommand(createResult.visitVerificationId(), 99999L);

            // when & then
            assertThatThrownBy(
                            () -> visitVerificationService.cancelvisitVerification(cancelCommand))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(VisitVerificationErrorCode.ACCESS_DINED);
        }

        @Test
        @DisplayName("실패: 당일 생성된 투표가 아니라면 취소할 수 없고 visitVerification_CANNOT_BE_CANCELLED 예외가 발생한다.")
        void cancelVisitVerification_fail_notToday() {
            // given: 투표 생성 후, 리플렉션을 이용해 강제로 날짜를 어제로 조작
            VisitVerificationCreateCommand createCommand =
                    new VisitVerificationCreateCommand(
                            userId, locationId, locationContentArtistId, snapshotName);
            VisitVerificationCreateResult createResult =
                    visitVerificationService.createvisitVerification(createCommand);

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
                            () -> visitVerificationService.cancelvisitVerification(cancelCommand))
                    .isInstanceOf(CustomException.class)
                    .extracting("errorCode")
                    .isEqualTo(VisitVerificationErrorCode.CANNOT_BE_CANCELLED);
        }

        @Test
        @DisplayName("성공: 이미 취소된 투표에 대해 다시 취소 요청을 보내도 예외 없이 정상 종료된다 (멱등성/방어 로직 검증).")
        void cancel_visitVerification_alreadyCancelled_successIgnored() {
            // given: 투표 생성
            VisitVerificationCreateCommand createCommand =
                    new VisitVerificationCreateCommand(
                            userId, locationId, locationContentArtistId, snapshotName);
            VisitVerificationCreateResult createResult =
                    visitVerificationService.createvisitVerification(createCommand);

            VisitVerificationCancelCommand cancelCommand =
                    new VisitVerificationCancelCommand(createResult.visitVerificationId(), userId);

            // 1차 취소
            visitVerificationService.cancelvisitVerification(cancelCommand);

            // when & then: 2차 취소 요청 시 예외가 발생하지 않고 그대로 통과되는지 확인
            assertThatCode(() -> visitVerificationService.cancelvisitVerification(cancelCommand))
                    .doesNotThrowAnyException();

            VisitVerification visitVerification =
                    visitVerificationRepository
                            .findById(createResult.visitVerificationId())
                            .orElseThrow();
            assertThat(visitVerification.getStatus()).isEqualTo(VisitVerificationStatus.CANCELED);
        }
    }
}
