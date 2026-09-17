package com.tracek.domain.fan.infrastructure.persistence.jpa;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.fan.domain.model.ContentFan;
import com.tracek.domain.fan.domain.model.ContentFanId;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ContentFanJpaRepositoryTest {

    @Autowired private ContentFanJpaRepository contentFanJpaRepository;

    @AfterEach
    void tearDown() {
        contentFanJpaRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자가 팬으로 등록한 콘텐츠 목록을 조회한다.")
    void findAllByUserId_success() {
        // given
        Long userId = 1L;

        contentFanJpaRepository.saveAndFlush(ContentFan.create(new ContentFanId(userId, 10L)));

        contentFanJpaRepository.saveAndFlush(ContentFan.create(new ContentFanId(userId, 20L)));

        contentFanJpaRepository.saveAndFlush(ContentFan.create(new ContentFanId(2L, 30L)));

        // when
        List<ContentFan> result = contentFanJpaRepository.findAllByUserId(userId);

        // then
        assertThat(result).hasSize(2);

        assertThat(result).extracting(ContentFan::getContentId).containsExactlyInAnyOrder(10L, 20L);
    }

    @Test
    @DisplayName("사용자가 팬으로 등록한 콘텐츠 수를 조회한다.")
    void countContentFanByUserId_success() {
        // given
        Long userId = 1L;

        contentFanJpaRepository.saveAndFlush(ContentFan.create(new ContentFanId(userId, 10L)));

        contentFanJpaRepository.saveAndFlush(ContentFan.create(new ContentFanId(userId, 20L)));

        contentFanJpaRepository.saveAndFlush(ContentFan.create(new ContentFanId(2L, 30L)));

        // when
        Integer result = contentFanJpaRepository.countContentFanByUserId(userId);

        // then
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("콘텐츠를 팬으로 등록한 사용자 수를 조회한다.")
    void countContentFanByContentId_success() {
        // given
        Long contentId = 10L;

        contentFanJpaRepository.saveAndFlush(ContentFan.create(new ContentFanId(1L, contentId)));

        contentFanJpaRepository.saveAndFlush(ContentFan.create(new ContentFanId(2L, contentId)));

        contentFanJpaRepository.saveAndFlush(ContentFan.create(new ContentFanId(3L, 20L)));

        // when
        Integer result = contentFanJpaRepository.countContentFanByContentId(contentId);

        // then
        assertThat(result).isEqualTo(2);
    }

    @Test
    @DisplayName("팬으로 등록한 콘텐츠가 없으면 빈 목록을 반환한다.")
    void findAllByUserId_empty() {
        // when
        List<ContentFan> result = contentFanJpaRepository.findAllByUserId(999L);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("팬이 없는 콘텐츠의 팬 수는 0이다.")
    void countContentFanByContentId_zero() {
        // when
        Integer result = contentFanJpaRepository.countContentFanByContentId(999L);

        // then
        assertThat(result).isZero();
    }
}
