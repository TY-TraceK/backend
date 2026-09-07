package com.tracek.domain.visitVerification.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class VisitVerificationQueryServiceImplTest {

    @Mock private VisitVerificationRepository visitVerificationRepository;

    @InjectMocks private VisitVerificationQueryServiceImpl visitVerificationQueryService;

    private Long userId;
    private Long locationId;
    private LocalDate targetDate;
    private VisitVerificationStatusSearchCondition condition;
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
        condition = new VisitVerificationStatusSearchCondition(userId, locationId, targetDate);
    }

    @Nested
    @DisplayName("나의 투표 상태 조회 테스트")
    class GetMyVisitVerificationStatusTest {

        @Test
        @DisplayName("성공: 해당 날짜에 투표 내역이 존재하면 isVerified가 true이고 투표 정보가 담긴 Result를 반환한다.")
        void getMy_visitVerificationStatus_success_visitVerified() {
            // given: 리포지토리가 투표 엔티티를 반환하도록 Mock 설정
            VisitVerification visitVerification =
                    VisitVerification.createvisitVerification(
                            userId,
                            VisitVerificationTarget.of(
                                    locationId,
                                    locationContentArtistId,
                                    artistId,
                                    contentId,
                                    snapshotName));
            // ID 값을 임의로 주입 (Reflection 활용)
            ReflectionTestUtils.setField(visitVerification, "id", 42L);

            given(
                            visitVerificationRepository.findUserLocationVerifiedByDate(
                                    userId, locationId, targetDate))
                    .willReturn(Optional.of(visitVerification));

            // when
            VisitVerificationStatusSearchResult result =
                    visitVerificationQueryService.getMyvisitVerificationStatus(condition);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isvisitVerificationd()).isTrue();
            assertThat(result.visitVerificationId()).isEqualTo(42L);
            assertThat(result.targetDate()).isEqualTo(targetDate);
        }

        @Test
        @DisplayName(
                "성공: 해당 날짜에 투표 내역이 존재하지 않으면 isVerified가 false이고 visitVerificationId가 null인 Result를 반환한다.")
        void getMy_visitVerificationStatus_success_notVerified() {
            // given: 리포지토리가 빈 Optional을 반환하도록 Mock 설정 (투표 안 함)
            given(
                            visitVerificationRepository.findUserLocationVerifiedByDate(
                                    userId, locationId, targetDate))
                    .willReturn(Optional.empty());

            // when
            VisitVerificationStatusSearchResult result =
                    visitVerificationQueryService.getMyvisitVerificationStatus(condition);

            // then
            assertThat(result).isNotNull();
            assertThat(result.isvisitVerificationd()).isFalse();
            assertThat(result.visitVerificationId()).isNull();
            assertThat(result.targetDate()).isEqualTo(targetDate);
        }
    }

    @Nested
    @DisplayName("나의 투표 이력 조회 테스트")
    class GetMyHistoriesTest {

        private Pageable pageable;
        private VisitVerificationHistoriesSearchCondition searchCondition;

        @BeforeEach
        void setUp() {
            pageable = PageRequest.of(0, 20);
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
                            .build();
        }

        @Test
        @DisplayName("성공: 검색 조건과 페이지 정보로 투표 이력을 조회한다.")
        void getMyHistories_success() {
            VisitVerification visitVerification =
                    VisitVerification.createvisitVerification(
                            userId,
                            VisitVerificationTarget.of(
                                    locationId,
                                    locationContentArtistId,
                                    artistId,
                                    contentId,
                                    snapshotName));

            ReflectionTestUtils.setField(visitVerification, "id", 42L);

            Page<VisitVerification> visitVerifications =
                    new PageImpl<>(List.of(visitVerification), pageable, 1);

            given(
                            visitVerificationRepository.findHistoriesByCriteria(
                                    any(VisitVerificationHistoryCriteria.class), eq(pageable)))
                    .willReturn(visitVerifications);

            // when
            VisitVerificationHistoriesResult result =
                    visitVerificationQueryService.getMyHistories(searchCondition, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.histories()).isNotEmpty();

            verify(visitVerificationRepository)
                    .findHistoriesByCriteria(
                            any(VisitVerificationHistoryCriteria.class), eq(pageable));
        }

        @Test
        @DisplayName("성공: 검색 조건에 값이 없어도 전체 투표 이력을 조회할 수 있다.")
        void getMyHistories_success_withoutFilter() {
            // given
            VisitVerificationHistoriesSearchCondition searchCondition =
                    VisitVerificationHistoriesSearchCondition.builder().userId(userId).build();

            Page<VisitVerification> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            given(
                            visitVerificationRepository.findHistoriesByCriteria(
                                    any(VisitVerificationHistoryCriteria.class), eq(pageable)))
                    .willReturn(emptyPage);

            // when
            VisitVerificationHistoriesResult result =
                    visitVerificationQueryService.getMyHistories(searchCondition, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.histories()).isEmpty();

            verify(visitVerificationRepository)
                    .findHistoriesByCriteria(
                            any(VisitVerificationHistoryCriteria.class), eq(pageable));
        }

        @Test
        @DisplayName("성공: Application Condition이 Domain Criteria로 올바르게 변환되어 Repository에 전달된다.")
        void getMyHistories_success_convertCriteria() {
            // given
            given(
                            visitVerificationRepository.findHistoriesByCriteria(
                                    any(VisitVerificationHistoryCriteria.class), eq(pageable)))
                    .willReturn(new PageImpl<>(List.of(), pageable, 0));

            // when
            visitVerificationQueryService.getMyHistories(searchCondition, pageable);

            // then
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
                                                            .equals(searchCondition.endDate())),
                            eq(pageable));
        }

        @Test
        @DisplayName("성공: 오름차순(ASC) 등 다른 정렬 조건이 요청되어도 QueryDSL 내부 정의에 따라 최신순으로 고정 처리된다.")
        void getMyHistories_success_ignoreExternalSort() {
            // given: 클라이언트가 임의로 오름차순(ASC) 정렬을 요청한 Pageable 생성
            Pageable requestedPageable =
                    PageRequest.of(
                            0,
                            20,
                            org.springframework.data.domain.Sort.by("verifiedAt").ascending());

            VisitVerification visitVerification =
                    VisitVerification.createvisitVerification(
                            userId,
                            VisitVerificationTarget.of(
                                    locationId,
                                    locationContentArtistId,
                                    artistId,
                                    contentId,
                                    snapshotName));

            ReflectionTestUtils.setField(visitVerification, "id", 42L);

            Page<VisitVerification> visitVerifications =
                    new PageImpl<>(List.of(visitVerification), requestedPageable, 1);

            given(
                            visitVerificationRepository.findHistoriesByCriteria(
                                    any(VisitVerificationHistoryCriteria.class),
                                    any(Pageable.class)))
                    .willReturn(visitVerifications);

            // when
            VisitVerificationHistoriesResult result =
                    visitVerificationQueryService.getMyHistories(
                            searchCondition, requestedPageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.histories()).isNotEmpty();

            // 레포지토리 호출 시 페이징 객체가 정상적으로 전달되었는지 검증
            verify(visitVerificationRepository)
                    .findHistoriesByCriteria(
                            any(VisitVerificationHistoryCriteria.class), eq(requestedPageable));
        }
    }
}
