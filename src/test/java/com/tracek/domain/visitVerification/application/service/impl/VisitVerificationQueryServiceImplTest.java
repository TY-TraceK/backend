package com.tracek.domain.visitVerification.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationHistoriesSearchCondition;
import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationStatusSearchCondition;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesIndividualResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationStatusSearchResult;
import com.tracek.domain.visitVerification.application.dto.result.VerificationLocationCandidateResult;
import com.tracek.domain.visitVerification.application.dto.result.VerificationLocationResult;
import com.tracek.domain.visitVerification.application.repository.VerificationLocationRepository;
import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationHistoryCriteria;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationTarget;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationView;
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

    @Mock private VerificationLocationRepository verificationLocationRepository;

    @InjectMocks private VisitVerificationQueryServiceImpl visitVerificationQueryService;

    private Long userId;
    private Long locationId;
    private LocalDate targetDate;
    private VisitVerificationStatusSearchCondition condition;
    private Long contentId;
    private Long artistId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        locationId = 100L;
        targetDate = LocalDate.of(2026, 8, 19);
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
                            userId,
                            locationId,
                            VisitVerificationTarget.of(List.of(artistId), contentId));

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

            LocalDate testDate = LocalDate.of(2026, 8, 19);

            LocalDateTime verifiedAt = testDate.atTime(12, 0);

            VisitVerificationView view =
                    VisitVerificationView.builder()
                            .visitVerificationId(42L)
                            .locationId(locationId)
                            .locationName("해운대해수욕장")
                            .locationAddress("부산광역시 해운대구")
                            .locationImageUrl("https://example.com/location.jpg")
                            .city("부산광역시")
                            .contentId(contentId)
                            .contentTitle("런닝맨")
                            .artists(
                                    List.of(
                                            VisitVerificationView.ArtistView.builder()
                                                    .artistId(artistId)
                                                    .artistName("유재석")
                                                    .build()))
                            .visitVerifiedTimeAt(testDate.atTime(12, 0))
                            .visitVerifiedTimeAt(verifiedAt)
                            .visitVerifiedDate(testDate)
                            .visitVerificationStatus(VisitVerificationStatus.VALID)
                            .build();

            given(
                            visitVerificationRepository.findHistoriesByCriteria(
                                    any(VisitVerificationHistoryCriteria.class)))
                    .willReturn(List.of(view));

            VisitVerificationHistoriesResult result =
                    visitVerificationQueryService.getMyHistories(searchCondition);

            assertThat(result).isNotNull();
            assertThat(result.histories()).hasSize(1);
            assertThat(result.histories()).containsKey(testDate);

            List<VisitVerificationHistoriesIndividualResult> histories =
                    result.histories().get(testDate);

            assertThat(histories).hasSize(1);

            VisitVerificationHistoriesIndividualResult history = histories.getFirst();

            assertThat(history.visitVerificationId()).isEqualTo(42L);

            assertThat(history.locationId()).isEqualTo(locationId);

            assertThat(history.locationName()).isEqualTo("해운대해수욕장");

            assertThat(history.locationAddress()).isEqualTo("부산광역시 해운대구");

            assertThat(history.contentId()).isEqualTo(contentId);

            assertThat(history.contentTitle()).isEqualTo("런닝맨");

            assertThat(history.artists()).hasSize(1);

            assertThat(history.artists().getFirst().artistId()).isEqualTo(artistId);

            assertThat(history.artists().getFirst().artistName()).isEqualTo("유재석");

            assertThat(history.visitVerifiedTimeAt()).isEqualTo(verifiedAt);

            assertThat(history.visitVerifiedDate()).isEqualTo(testDate);

            assertThat(history.visitVerificationStatus())
                    .isEqualTo(VisitVerificationStatus.VALID.name());

            assertThat(result.hasNext()).isFalse();

            assertThat(result.nextCursorDate()).isEqualTo(testDate);

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
            assertThat(result.hasNext()).isFalse();
            assertThat(result.nextCursorDate()).isNull();

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
                            argThat(
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
    @Nested
    @DisplayName("방문 인증 위치 후보 조회 테스트")
    class GetVerificationLocationCandidatesTest {

        @Test
        @DisplayName("부산 내부 좌표이면 true와 빈 위치 후보를 반환한다.")
        void getCandidates_inBusan() {
            VerificationLocationCandidateResult candidate =
                    new VerificationLocationCandidateResult(
                            "부산 구 백제병원", 35.115, 129.04, "https://example.com/location.jpg");
            VerificationLocationResult result =
                    visitVerificationQueryService.getVerificationLocationCandidates(35.1796, 129.0756);

            assertThat(result.isInBusan()).isTrue();
            assertThat(result.locations()).isEmpty();
            verify(verificationLocationRepository, never()).findVerificationLocationCandidates();
        }

        @Test
        @DisplayName("부산 외부 좌표이면 false와 지정 위치 후보를 반환한다.")
        void getCandidates_outsideBusan() {
            VerificationLocationCandidateResult candidate =
                    new VerificationLocationCandidateResult(
                            "부산 구 백제병원", 35.115, 129.04, "https://example.com/location.jpg");
            given(verificationLocationRepository.findVerificationLocationCandidates())
                    .willReturn(List.of(candidate));

            VerificationLocationResult result =
                    visitVerificationQueryService.getVerificationLocationCandidates(37.5665, 126.9780);

            assertThat(result.isInBusan()).isFalse();
            assertThat(result.locations()).containsExactly(candidate);
            verify(verificationLocationRepository).findVerificationLocationCandidates();
        }
    }
}
