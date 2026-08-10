package com.tracek.domain.vote.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;

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

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // H2 인메모리 DB 사용
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
}
