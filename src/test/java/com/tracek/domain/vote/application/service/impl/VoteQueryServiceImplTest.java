package com.tracek.domain.vote.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.vote.application.dto.command.VoteCreateCommand;
import com.tracek.domain.vote.application.dto.condition.VoteStatusSearchCondition;
import com.tracek.domain.vote.application.dto.result.VoteStatusSearchResult;
import com.tracek.domain.vote.domain.model.Vote;
import com.tracek.domain.vote.domain.model.VoteTarget;
import com.tracek.domain.vote.domain.repository.VoteRepository;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class VoteQueryServiceImplTest {

    @Mock private VoteRepository voteRepository;

    @InjectMocks private VoteQueryServiceImpl voteQueryService;

    private Long userId;
    private Long locationId;
    private LocalDate targetDate;
    private VoteStatusSearchCondition condition;
    private String snapshotName;
    private Long contentId;
    private Long artistId;
    private Long locationContentArtistId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        locationId = 100L;
        targetDate = LocalDate.of(2026, 8, 19);
        locationContentArtistId = 1000L;
        condition = new VoteStatusSearchCondition(userId, locationId, targetDate);

        VoteCreateCommand command =
                new VoteCreateCommand(userId, locationId, locationContentArtistId, snapshotName);

        userId = 1L;
        locationId = 100L;
        snapshotName = "경복궁 | BTS | Run BTS Ep.100";
    }

    @Nested
    @DisplayName("나의 투표 상태 조회 테스트")
    class GetMyVoteStatusTest {

        @Test
        @DisplayName("성공: 해당 날짜에 투표 내역이 존재하면 isVoted가 true이고 투표 정보가 담긴 Result를 반환한다.")
        void getMyVoteStatus_success_voted() {
            // given: 리포지토리가 투표 엔티티를 반환하도록 Mock 설정
            Vote vote =
                    Vote.createVote(
                            userId,
                            VoteTarget.of(
                                    locationId,
                                    locationContentArtistId,
                                    artistId,
                                    contentId,
                                    snapshotName));
            // ID 값을 임의로 주입 (Reflection 활용)
            ReflectionTestUtils.setField(vote, "id", 42L);

            given(voteRepository.findUserLocationVoteByDate(userId, locationId, targetDate))
                    .willReturn(Optional.of(vote));

            // when
            VoteStatusSearchResult result = voteQueryService.getMyVoteStatus(condition);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isVoted()).isTrue();
            assertThat(result.voteId()).isEqualTo(42L);
            assertThat(result.targetDate()).isEqualTo(targetDate);
        }

        @Test
        @DisplayName("성공: 해당 날짜에 투표 내역이 존재하지 않으면 isVoted가 false이고 voteId가 null인 Result를 반환한다.")
        void getMyVoteStatus_success_notVoted() {
            // given: 리포지토리가 빈 Optional을 반환하도록 Mock 설정 (투표 안 함)
            given(voteRepository.findUserLocationVoteByDate(userId, locationId, targetDate))
                    .willReturn(Optional.empty());

            // when
            VoteStatusSearchResult result = voteQueryService.getMyVoteStatus(condition);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isVoted()).isFalse();
            assertThat(result.voteId()).isNull();
            assertThat(result.targetDate()).isEqualTo(targetDate);
        }
    }
}
