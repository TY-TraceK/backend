package com.tracek.domain.vote.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.location.application.dto.LocationContentArtistResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.vote.application.dto.command.VoteCreateCommand;
import com.tracek.domain.vote.application.dto.result.VoteCreateResult;
import com.tracek.domain.vote.domain.exception.VoteErrorCode;
import com.tracek.domain.vote.domain.model.Vote;
import com.tracek.domain.vote.domain.repository.VoteRepository;
import com.tracek.global.exception.CustomException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class VoteServiceImplTest {

    @Autowired private VoteCommandService voteService;

    @Autowired private VoteRepository voteRepository;

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
        voteRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("성공: 투표 생성 요청 시 정상적으로 투표가 저장되고 Result가 반환된다.")
    void createVote_success() {
        // given
        VoteCreateCommand command =
                new VoteCreateCommand(userId, locationId, locationContentArtistId, snapshotName);

        // when
        VoteCreateResult result = voteService.createVote(command);

        // then
        assertThat(result).isNotNull();

        List<Vote> votes = voteRepository.findAll();
        assertThat(votes).hasSize(1);
        Vote savedVote = votes.getFirst();
        assertThat(savedVote.getVoteOwner()).isEqualTo(userId);
        assertThat(savedVote.getVoteTarget().getLocationId()).isEqualTo(locationId);
    }

    @Test
    @DisplayName("실패: 이미 투표한 관광지에 다시 투표를 시도하면 AlreadyVotedException이 발생한다.")
    void createVote_fail_alreadyVoted() {
        // given: 1차 투표 진행
        VoteCreateCommand command =
                new VoteCreateCommand(userId, locationId, locationContentArtistId, snapshotName);
        voteService.createVote(command);

        // when & then: 동일 커맨드로 2차 투표 시도시 예외 발생
        assertThatThrownBy(() -> voteService.createVote(command))
                .isInstanceOf(CustomException.class)
                .extracting("errorCode")
                .isEqualTo(VoteErrorCode.ALREADY_VOTED);
    }

    @Test
    @DisplayName("동시성: 동일한 유저가 동시에 2개의 스레드로 투표를 요청하면 1건만 성공하고 1건은 실패한다.")
    void createVote_concurrency_twoThreads() throws InterruptedException {
        int threadCount = 2;
        AtomicInteger successCount;
        AtomicInteger failCount;
        try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch endLatch = new CountDownLatch(threadCount);

            VoteCreateCommand command =
                    new VoteCreateCommand(
                            userId, locationId, locationContentArtistId, snapshotName);

            successCount = new AtomicInteger(0);
            failCount = new AtomicInteger(0);

            for (int i = 0; i < threadCount; i++) {
                final int threadIndex = i;
                executorService.submit(
                        () -> {
                            try {
                                startLatch.await();
                                voteService.createVote(command);
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
                                System.out.println(">>> [스레드 " + threadIndex + "] DB 유니크 충돌 발생!");
                                failCount.incrementAndGet();
                            } catch (Throwable t) {
                                // 이 부분이 범인입니다!
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
