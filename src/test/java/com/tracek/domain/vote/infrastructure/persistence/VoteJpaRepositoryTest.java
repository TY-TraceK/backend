package com.tracek.domain.vote.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tracek.domain.vote.domain.enums.VoteStatus;
import com.tracek.domain.vote.domain.model.Vote;
import com.tracek.domain.vote.domain.model.VoteTarget;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class VoteJpaRepositoryTest {

    private Long voteOwner;
    private VoteTarget voteTarget;

    @Autowired private VoteJpaRepository voteJpaRepository;

    @Autowired private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        voteOwner = 1L;
        voteTarget = VoteTarget.of(100L, 1000L, 10L, 20L, "경복궁 | BTS | Run BTS Ep.100");
    }

    @Test
    @DisplayName("DB에 저장 하면 가상 칼럼이 생성되고 취소(invalid)하면 validVotedAt 가상 컬럼이 NULL이 된다.")
    void validVotedAt_generated_column_test() {
        // given
        Vote vote = Vote.createVote(voteOwner, voteTarget);
        Vote savedVote = voteJpaRepository.save(vote);

        entityManager.flush();
        entityManager.clear();

        // 1. VALID 저장 시 validVotedAt은 votedAt 날짜와 동일하게 저장됨
        Vote foundVote = voteJpaRepository.findById(savedVote.getId()).orElseThrow();
        assertThat(foundVote.getValidVotedAt()).isEqualTo(LocalDate.now());

        // 2. CANCELED 로 변경 후 DB flush
        foundVote.invalid();
        entityManager.flush();
        entityManager.clear();

        // 3. 재조회 시 DB가 valid_voted_at을 NULL로 평가했는지 검증
        Vote canceledVote = voteJpaRepository.findById(savedVote.getId()).orElseThrow();
        assertThat(canceledVote.getVoteStatus()).isEqualTo(VoteStatus.CANCELED);
        assertThat(canceledVote.getValidVotedAt()).isNull();
    }

    @Test
    @DisplayName("동일 유저가 동일 장소에 VALID 상태로 중복 저장 시 DB 유니크 제약조건 위반 예외가 발생한다.")
    void duplicate_vote_throws_DataIntegrityViolationException() {
        // given: 1차 투표 정상 저장
        Vote vote1 = Vote.createVote(voteOwner, voteTarget);
        voteJpaRepository.save(vote1);
        entityManager.flush();

        // when & then: 동일한 조건의 2차 투표 저장 시 save() 호출 시점에 예외 발생 검증
        Vote vote2 = Vote.createVote(voteOwner, voteTarget);

        assertThatThrownBy(() -> voteJpaRepository.saveAndFlush(vote2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("기존 투표를 CANCELED(invalid) 처리하면 동일 유저가 동일 장소에 다시 유효한 투표를 저장할 수 있다.")
    void revote_success_after_cancellation() {
        // given: 1차 투표 후 취소 처리
        Vote vote1 = Vote.createVote(voteOwner, voteTarget);
        voteJpaRepository.save(vote1);
        entityManager.flush();

        vote1.invalid();
        entityManager.flush();
        entityManager.clear();

        // when: 2차 재투표 저장
        Vote revote = Vote.createVote(voteOwner, voteTarget);
        Vote savedRevote = voteJpaRepository.save(revote);
        entityManager.flush();
        entityManager.clear();

        // then: 중복 예외 없이 정상 저장되고 validVotedAt이 세팅됨
        Vote foundRevote = voteJpaRepository.findById(savedRevote.getId()).orElseThrow();
        assertThat(foundRevote.getVoteStatus()).isEqualTo(VoteStatus.VALID);
        assertThat(foundRevote.getValidVotedAt()).isEqualTo(LocalDate.now());
    }
}
