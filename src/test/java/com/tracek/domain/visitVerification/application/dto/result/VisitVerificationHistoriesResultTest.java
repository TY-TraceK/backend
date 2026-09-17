package com.tracek.domain.visitVerification.application.dto.result;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationView;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VisitVerificationHistoriesResultTest {

  @Test
  @DisplayName("조회 결과가 없으면 빈 이력과 hasNext false를 반환한다")
  void of_empty() {

    VisitVerificationHistoriesResult result =
        VisitVerificationHistoriesResult.of(
            List.of(),
            10);

    assertThat(result.histories()).isEmpty();
    assertThat(result.hasNext()).isFalse();
    assertThat(result.nextCursorDate()).isNull();
  }

  @Test
  @DisplayName("같은 날짜 방문 인증은 하나의 날짜 그룹으로 묶인다")
  void of_groupByDate() {

    LocalDate date = LocalDate.of(2026, 9, 18);

    VisitVerificationView first =
        createView(
            1L,
            date,
            LocalDateTime.of(
                2026,
                9,
                18,
                9,
                0));

    VisitVerificationView second =
        createView(
            2L,
            date,
            LocalDateTime.of(
                2026,
                9,
                18,
                11,
                0));

    VisitVerificationHistoriesResult result =
        VisitVerificationHistoriesResult.of(
            List.of(first, second),
            10);

    assertThat(result.histories()).hasSize(1);
    assertThat(result.histories()).containsKey(date);

    assertThat(result.histories().get(date))
        .hasSize(2);

    assertThat(result.hasNext()).isFalse();

    assertThat(result.nextCursorDate())
        .isEqualTo(date);
  }

  @Test
  @DisplayName("여러 날짜의 방문 인증을 날짜별로 그룹화한다")
  void of_multipleDates() {

    LocalDate firstDate =
        LocalDate.of(2026, 9, 18);

    LocalDate secondDate =
        LocalDate.of(2026, 9, 17);

    VisitVerificationView first =
        createView(
            1L,
            firstDate,
            firstDate.atTime(10, 0));

    VisitVerificationView second =
        createView(
            2L,
            secondDate,
            secondDate.atTime(10, 0));

    VisitVerificationHistoriesResult result =
        VisitVerificationHistoriesResult.of(
            List.of(first, second),
            10);

    assertThat(result.histories()).hasSize(2);

    assertThat(result.histories())
        .containsKeys(
            firstDate,
            secondDate);

    assertThat(result.hasNext()).isFalse();

    assertThat(result.nextCursorDate())
        .isEqualTo(secondDate);
  }

  @Test
  @DisplayName("조회된 날짜 수가 요청 size보다 많으면 hasNext가 true이다")
  void of_hasNext() {

    LocalDate firstDate =
        LocalDate.of(2026, 9, 18);

    LocalDate secondDate =
        LocalDate.of(2026, 9, 17);

    LocalDate thirdDate =
        LocalDate.of(2026, 9, 16);

    VisitVerificationView first =
        createView(
            1L,
            firstDate,
            firstDate.atTime(10, 0));

    VisitVerificationView second =
        createView(
            2L,
            secondDate,
            secondDate.atTime(10, 0));

    VisitVerificationView third =
        createView(
            3L,
            thirdDate,
            thirdDate.atTime(10, 0));

    VisitVerificationHistoriesResult result =
        VisitVerificationHistoriesResult.of(
            List.of(
                first,
                second,
                third),
            2);

    assertThat(result.hasNext()).isTrue();

    assertThat(result.histories())
        .hasSize(2);

    assertThat(result.histories())
        .containsKeys(
            firstDate,
            secondDate);

    assertThat(result.histories())
        .doesNotContainKey(thirdDate);

    assertThat(result.nextCursorDate())
        .isEqualTo(secondDate);
  }

  @Test
  @DisplayName("size 경계 날짜에 여러 이력이 있어도 해당 날짜 이력을 모두 포함한다")
  void of_includeAllHistoriesOfLastAllowedDate() {

    LocalDate firstDate =
        LocalDate.of(2026, 9, 18);

    LocalDate secondDate =
        LocalDate.of(2026, 9, 17);

    LocalDate thirdDate =
        LocalDate.of(2026, 9, 16);

    VisitVerificationView first =
        createView(
            1L,
            firstDate,
            firstDate.atTime(10, 0));

    VisitVerificationView second1 =
        createView(
            2L,
            secondDate,
            secondDate.atTime(9, 0));

    VisitVerificationView second2 =
        createView(
            3L,
            secondDate,
            secondDate.atTime(18, 0));

    VisitVerificationView third =
        createView(
            4L,
            thirdDate,
            thirdDate.atTime(10, 0));

    VisitVerificationHistoriesResult result =
        VisitVerificationHistoriesResult.of(
            List.of(
                first,
                second1,
                second2,
                third),
            2);

    assertThat(result.hasNext()).isTrue();

    assertThat(result.histories())
        .hasSize(2);

    assertThat(
        result.histories()
            .get(secondDate))
        .hasSize(2);

    assertThat(result.histories())
        .doesNotContainKey(thirdDate);

    assertThat(result.nextCursorDate())
        .isEqualTo(secondDate);
  }

  @Test
  @DisplayName("Result 변환 과정에서도 좋아요와 보관 여부가 유지된다")
  void of_likeAndArchive() {

    LocalDate date =
        LocalDate.of(2026, 9, 18);

    VisitVerificationView view =
        VisitVerificationView.builder()
            .visitVerificationId(1L)
            .locationId(10L)
            .locationName("LOCATION")
            .locationAddress("ADDRESS")
            .locationImageUrl("IMAGE")
            .city("부산광역시")
            .contentId(20L)
            .contentTitle("CONTENT")
            .artists(List.of())
            .visitVerifiedDate(date)
            .visitVerifiedTimeAt(
                date.atTime(10, 0))
            .visitVerificationStatus(
                VisitVerificationStatus.VALID)
            .liked(true)
            .archived(true)
            .build();

    VisitVerificationHistoriesResult result =
        VisitVerificationHistoriesResult.of(
            List.of(view),
            10);

    VisitVerificationHistoriesIndividualResult history =
        result.histories()
            .get(date)
            .getFirst();

    assertThat(history.liked()).isTrue();
    assertThat(history.archived()).isTrue();
  }

  private VisitVerificationView createView(
      Long id,
      LocalDate date,
      LocalDateTime verifiedAt) {

    return VisitVerificationView.builder()
        .visitVerificationId(id)
        .locationId(id * 10)
        .locationName("LOCATION_" + id)
        .locationAddress("ADDRESS_" + id)
        .locationImageUrl("IMAGE_" + id)
        .city("부산광역시")
        .contentId(id * 100)
        .contentTitle("CONTENT_" + id)
        .artists(List.of())
        .visitVerifiedDate(date)
        .visitVerifiedTimeAt(verifiedAt)
        .visitVerificationStatus(
            VisitVerificationStatus.VALID)
        .liked(false)
        .archived(false)
        .build();
  }
}