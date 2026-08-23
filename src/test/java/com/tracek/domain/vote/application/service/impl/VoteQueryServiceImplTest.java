package com.tracek.domain.vote.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.tracek.domain.vote.application.dto.condition.VoteHistoriesSearchCondition;
import com.tracek.domain.vote.application.dto.condition.VoteStatusSearchCondition;
import com.tracek.domain.vote.application.dto.result.VoteHistoriesResult;
import com.tracek.domain.vote.application.dto.result.VoteStatusSearchResult;
import com.tracek.domain.vote.domain.enums.VoteStatus;
import com.tracek.domain.vote.domain.model.Vote;
import com.tracek.domain.vote.domain.model.VoteHistoryCriteria;
import com.tracek.domain.vote.domain.model.VoteTarget;
import com.tracek.domain.vote.domain.repository.VoteRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
        contentId = 10L;
        artistId = 5L;
        snapshotName = "경복궁 | BTS | Run BTS Ep.100";
        condition = new VoteStatusSearchCondition(userId, locationId, targetDate);
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

    @Nested
    @DisplayName("나의 투표 이력 조회 테스트")
    class GetMyHistoriesTest {

        private Pageable pageable;
        private VoteHistoriesSearchCondition searchCondition;

        @BeforeEach
        void setUp() {
            pageable = PageRequest.of(0, 20);
            LocalDateTime startDate = LocalDateTime.of(2026, 8, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2026, 9, 1, 0, 0);

            searchCondition =
                    VoteHistoriesSearchCondition.builder()
                            .userId(userId)
                            .artistId(artistId)
                            .contentId(contentId)
                            .voteStatus(VoteStatus.VALID)
                            .locationId(locationId)
                            .startDate(startDate)
                            .endDate(endDate)
                            .build();
        }

        @Test
        @DisplayName("성공: 검색 조건과 페이지 정보로 투표 이력을 조회한다.")
        void getMyHistories_success() {
            Vote vote =
                    Vote.createVote(
                            userId,
                            VoteTarget.of(
                                    locationId,
                                    locationContentArtistId,
                                    artistId,
                                    contentId,
                                    snapshotName));

            ReflectionTestUtils.setField(vote, "id", 42L);

            Page<Vote> votes = new PageImpl<>(List.of(vote), pageable, 1);

            given(
                            voteRepository.findHistoriesByCriteria(
                                    any(VoteHistoryCriteria.class), eq(pageable)))
                    .willReturn(votes);

            // when
            VoteHistoriesResult result = voteQueryService.getMyHistories(searchCondition, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.histories()).isNotEmpty();

            verify(voteRepository)
                    .findHistoriesByCriteria(any(VoteHistoryCriteria.class), eq(pageable));
        }

        @Test
        @DisplayName("성공: 검색 조건에 값이 없어도 전체 투표 이력을 조회할 수 있다.")
        void getMyHistories_success_withoutFilter() {
            // given
            VoteHistoriesSearchCondition searchCondition =
                    VoteHistoriesSearchCondition.builder().userId(userId).build();

            Page<Vote> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            given(
                            voteRepository.findHistoriesByCriteria(
                                    any(VoteHistoryCriteria.class), eq(pageable)))
                    .willReturn(emptyPage);

            // when
            VoteHistoriesResult result = voteQueryService.getMyHistories(searchCondition, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.histories()).isEmpty();

            verify(voteRepository)
                    .findHistoriesByCriteria(any(VoteHistoryCriteria.class), eq(pageable));
        }

        @Test
        @DisplayName("성공: Application Condition이 Domain Criteria로 올바르게 변환되어 Repository에 전달된다.")
        void getMyHistories_success_convertCriteria() {
            // given
            given(
                            voteRepository.findHistoriesByCriteria(
                                    any(VoteHistoryCriteria.class), eq(pageable)))
                    .willReturn(new PageImpl<>(List.of(), pageable, 0));

            // when
            voteQueryService.getMyHistories(searchCondition, pageable);

            // then
            verify(voteRepository)
                    .findHistoriesByCriteria(
                            org.mockito.ArgumentMatchers.argThat(
                                    criteria ->
                                            criteria.userId().equals(searchCondition.userId())
                                                    && criteria.artistId()
                                                            .equals(searchCondition.artistId())
                                                    && criteria.contentId()
                                                            .equals(searchCondition.contentId())
                                                    && criteria.locationId()
                                                            .equals(searchCondition.locationId())
                                                    && criteria.voteStatus()
                                                            .equals(searchCondition.voteStatus())
                                                    && criteria.startDate()
                                                            .equals(searchCondition.startDate())
                                                    && criteria.endDate()
                                                            .equals(searchCondition.endDate())),
                            eq(pageable));
        }
    }
}
