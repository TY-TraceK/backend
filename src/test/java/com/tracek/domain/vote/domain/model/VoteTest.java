package com.tracek.domain.vote.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.vote.domain.enums.VoteStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Vote 엔티티 단위 테스트")
class VoteTest {

    private Long voteOwner;
    private VoteTarget voteTarget;

    @BeforeEach
    void setUp() {
        voteOwner = 1L;
        voteTarget = VoteTarget.of(100L, 1000L, 10L, 20L, "경복궁 | BTS | Run BTS Ep.100");
    }

    @Nested
    @DisplayName("투표 생성 테스트")
    class CreateVoteTest {

        @Test
        @DisplayName("createVote 정적 팩토리 메서드로 투표를 성공적으로 생성한다.")
        void createVote_success() {
            // when
            Vote vote = Vote.createVote(voteOwner, voteTarget);

            // then
            assertThat(vote).isNotNull();
            assertThat(vote.getVoteOwner()).isEqualTo(voteOwner);
            assertThat(vote.getVoteTarget()).isEqualTo(voteTarget);
            assertThat(vote.getVotedAt()).isEqualTo(LocalDateTime.now());
            assertThat(vote.getVoteStatus()).isEqualTo(VoteStatus.VALID);
        }

        @Test
        @DisplayName("생성자로 직접 투표 객체를 올바르게 생성한다.")
        void constructor_success() {

            // when
            Vote vote = Vote.createVote(voteOwner, voteTarget);

            // then
            assertThat(vote.getVoteOwner()).isEqualTo(voteOwner);
            assertThat(vote.getVoteTarget()).isEqualTo(voteTarget);
            assertThat(vote.getVoteStatus()).isEqualTo(VoteStatus.VALID);
        }
    }

    @Nested
    @DisplayName("투표 상태 및 무효화 테스트")
    class VoteStatusTest {

        @Test
        @DisplayName("invalid() 호출 시 투표 상태가 CANCELED로 변경된다.")
        void invalid_success() {
            // given
            Vote vote = Vote.createVote(voteOwner, voteTarget);
            assertThat(vote.getVoteStatus()).isEqualTo(VoteStatus.VALID);

            // when
            vote.invalid();

            // then
            assertThat(vote.getVoteStatus()).isEqualTo(VoteStatus.CANCELED);
        }

        @Test
        @DisplayName("투표 상태에 따른 validVotedAt 가상 컬럼 동작을 검증한다.")
        void validVotedAt_behavior_by_status() {
            // given
            LocalDateTime today = LocalDateTime.now();
            Vote validVote = Vote.createVote(voteOwner, voteTarget);

            // 1. VALID 상태일 때: DB 트리거 계산 로직상 validVotedAt은 votedAt과 동일
            assertThat(validVote.getVoteStatus()).isEqualTo(VoteStatus.VALID);
            assertThat(validVote.getVotedAt()).isEqualTo(today);

            // 2. invalid()로 취소(CANCELED) 시 상태 전환 검증
            validVote.invalid();
            assertThat(validVote.getVoteStatus()).isEqualTo(VoteStatus.CANCELED);
            // DB 적용 후 valid_voted_at은 NULL로 자동 평가되어 UNIQUE 제약에서 제외됨
        }
    }
}
