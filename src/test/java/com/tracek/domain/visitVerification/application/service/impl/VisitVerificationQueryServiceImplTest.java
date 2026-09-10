package com.tracek.domain.visitVerification.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationHistoriesSearchCondition;
import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationStatusSearchCondition;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationStatusSearchResult;
import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationHistoryCriteria;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationTarget;
import com.tracek.domain.visitVerification.domain.repository.VisitVerificationRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class VisitVerificationQueryServiceImplTest {

    @Mock private VisitVerificationRepository visitVerificationRepository;

    @InjectMocks private VisitVerificationQueryServiceImpl visitVerificationQueryService;

    private Long userId;
    private Long locationId;
    private LocalDate targetDate;
    private VisitVerificationStatusSearchCondition condition;
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
        condition = new VisitVerificationStatusSearchCondition(userId, locationId, targetDate);
    }

    @Nested
    @DisplayName("나의 방문 인증 상태 조회 테스트")
    class GetMyVisitVerificationStatusTest {

        @Test
        @DisplayName("성공: 해당 날짜에 방문 인증 내역이 존재하면 방문 인증 정보가 담긴 Result를 반환한다.")
        void getMy_visitVerificationStatus_success_visitVerified() {
            VisitVerification visitVerification =
                    VisitVerification.createvisitVerification(
                            userId, locationId, VisitVerificationTarget.of(artistId, contentId));
            ReflectionTestUtils.setField(visitVerification, "id", 42L);

            given(
                            visitVerificationRepository.findUserLocationVerifiedByDate(
                                    userId, locationId, targetDate))
                    .willReturn(Optional.of(visitVerification));

            VisitVerificationStatusSearchResult result =
                    visitVerificationQueryService.getMyVisitVerificationStatus(condition);

            assertThat(result).isNotNull();
            assertThat(result.isVisitVerified()).isTrue();
            assertThat(result.visitVerificationId()).isEqualTo(42L);
            assertThat(result.targetDate()).isEqualTo(targetDate);
        }

        @Test
        @DisplayName("성공: 해당 날짜에 방문 인증 내역이 존재하지 않으면 visitVerificationId가 null인 Result를 반환한다.")
        void getMy_visitVerificationStatus_success_notVerified() {
            given(
                            visitVerificationRepository.findUserLocationVerifiedByDate(
                                    userId, locationId, targetDate))
                    .willReturn(Optional.empty());

            VisitVerificationStatusSearchResult result =
                    visitVerificationQueryService.getMyVisitVerificationStatus(condition);

            assertThat(result).isNotNull();
            assertThat(result.isVisitVerified()).isFalse();
            assertThat(result.visitVerificationId()).isNull();
            assertThat(result.targetDate()).isEqualTo(targetDate);
        }
    }

    @Nested
    @DisplayName("나의 방문 인증 이력 조회 테스트")
    class GetMyHistoriesTest {

        private VisitVerificationHistoriesSearchCondition searchCondition;

        @BeforeEach
        void setUp() {
            LocalDateTime startDate = LocalDateTime.of(2026, 8, 1, 0, 0);
            LocalDateTime endDate = LocalDateTime.of(2026, 9, 1, 0, 0);

            searchCondition =
                    VisitVerificationHistoriesSearchCondition.builder()
                            .userId(userId)
                            .artistId(artistId)
                            .contentId(contentId)
                            .status(VisitVerificationStatus.VALID)
                            .locationId(locationId)
                            .startDate(startDate)
                            .endDate(endDate)
                            .size(20)
                            .build();
        }

        @Test
        @DisplayName("성공: 검색 조건과 날짜 그룹 size로 방문 인증 이력을 조회한다.")
        void getMyHistories_success() {
            VisitVerification visitVerification =
                    VisitVerification.createvisitVerification(
                            userId, locationId, VisitVerificationTarget.of(artistId, contentId));

            LocalDate testDate = LocalDate.of(2026, 8, 19);

            ReflectionTestUtils.setField(visitVerification, "id", 42L);
            ReflectionTestUtils.setField(visitVerification, "verifiedAt", testDate.atTime(12, 0));
            ReflectionTestUtils.setField(visitVerification, "validVerifiedAt", testDate);
            given(
                            visitVerificationRepository.findHistoriesByCriteria(
                                    any(VisitVerificationHistoryCriteria.class)))
                    .willReturn(List.of(visitVerification));

            VisitVerificationHistoriesResult result =
                    visitVerificationQueryService.getMyHistories(searchCondition);

            assertThat(result).isNotNull();
            assertThat(result.histories()).isNotEmpty();

            verify(visitVerificationRepository)
                    .findHistoriesByCriteria(any(VisitVerificationHistoryCriteria.class));
        }

        @Test
        @DisplayName("성공: 검색 조건에 값이 없어도 전체 방문 인증 이력을 조회할 수 있다.")
        void getMyHistories_success_withoutFilter() {
            VisitVerificationHistoriesSearchCondition emptySearchCondition =
                    VisitVerificationHistoriesSearchCondition.builder()
                            .userId(userId)
                            .size(20)
                            .build();

            given(
                            visitVerificationRepository.findHistoriesByCriteria(
                                    any(VisitVerificationHistoryCriteria.class)))
                    .willReturn(List.of());

            VisitVerificationHistoriesResult result =
                    visitVerificationQueryService.getMyHistories(emptySearchCondition);

            assertThat(result).isNotNull();
            assertThat(result.histories()).isEmpty();

            verify(visitVerificationRepository)
                    .findHistoriesByCriteria(any(VisitVerificationHistoryCriteria.class));
        }

        @Test
        @DisplayName("성공: Application Condition이 Domain Criteria로 올바르게 변환되어 Repository에 전달된다.")
        void getMyHistories_success_convertCriteria() {
            given(
                            visitVerificationRepository.findHistoriesByCriteria(
                                    any(VisitVerificationHistoryCriteria.class)))
                    .willReturn(List.of());

            visitVerificationQueryService.getMyHistories(searchCondition);

            verify(visitVerificationRepository)
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
                                                    && criteria.status()
                                                            .equals(searchCondition.status())
                                                    && criteria.startDate()
                                                            .equals(searchCondition.startDate())
                                                    && criteria.endDate()
                                                            .equals(searchCondition.endDate())
                                                    && criteria.size()
                                                            .equals(searchCondition.size())));
        }
    }
}
