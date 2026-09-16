package com.tracek.domain.fan.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.fan.domain.model.ArtistFan;
import com.tracek.domain.fan.domain.model.ArtistFanId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;

@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ArtistFanConcurrencyTest {

    private static final int CONCURRENT_REQUEST_COUNT = 10;

    @Autowired private ArtistFanJpaRepository artistFanJpaRepository;

    @AfterEach
    void tearDown() {
        artistFanJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("동시에 동일한 팬을 등록해도 UNIQUE 제약에 의해 하나만 저장된다")
    void concurrentInsert() throws InterruptedException {

        // given
        Long userId = 1L;
        Long artistId = 10L;

        int threadCount = CONCURRENT_REQUEST_COUNT;

        CountDownLatch startLatch = new CountDownLatch(1);

        CountDownLatch endLatch = new CountDownLatch(threadCount);

        AtomicInteger successCount = new AtomicInteger();

        AtomicInteger duplicateCount = new AtomicInteger();

        AtomicInteger unexpectedFailCount = new AtomicInteger();

        long startTime = System.currentTimeMillis();

        // when
        try (ExecutorService executorService = Executors.newFixedThreadPool(threadCount)) {

            for (int i = 0; i < threadCount; i++) {

                final int threadIdx = i + 1;

                executorService.submit(
                        () -> {
                            try {

                                startLatch.await();

                                artistFanJpaRepository.saveAndFlush(
                                        ArtistFan.create(new ArtistFanId(userId, artistId)));

                                successCount.incrementAndGet();

                            } catch (DataIntegrityViolationException e) {
                                duplicateCount.incrementAndGet();

                                System.out.println("[스레드 " + threadIdx + "] 중복 팬 등록 차단");

                            } catch (Throwable throwable) {
                                unexpectedFailCount.incrementAndGet();

                                System.err.println(
                                        "[스레드 "
                                                + threadIdx
                                                + "] 예상하지 못한 실패: "
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
        long fanCount = artistFanJpaRepository.countByUserIdAndArtistId(userId, artistId);

        long totalFanCount = artistFanJpaRepository.count();

        System.out.printf(
                """
            ===== Artist Fan 동시 생성 결과 =====
            실행 소요 시간 = %d ms
            요청 수 = %d
            INSERT 성공 수 = %d
            UNIQUE 중복 차단 수 = %d
            예상하지 못한 실패 수 = %d
            해당 Artist Fan Row 수 = %d
            전체 Artist Fan Row 수 = %d
            ===================================
            %n""",
                (endTime - startTime),
                threadCount,
                successCount.get(),
                duplicateCount.get(),
                unexpectedFailCount.get(),
                fanCount,
                totalFanCount);
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(duplicateCount.get()).isEqualTo(threadCount - 1);
        assertThat(unexpectedFailCount.get()).isZero();
        assertThat(fanCount).isEqualTo(1L);

        assertThat(totalFanCount).isEqualTo(1L);
    }
}
