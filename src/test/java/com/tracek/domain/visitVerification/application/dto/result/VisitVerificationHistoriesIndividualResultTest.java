package com.tracek.domain.visitVerification.application.dto.result;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationView;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class VisitVerificationHistoriesIndividualResultTest {

  @Test
  @DisplayName("VisitVerificationView를 방문 인증 이력 Result로 변환한다")
  void from_success() {

    LocalDate verifiedDate = LocalDate.of(2026, 9, 18);
    LocalDateTime verifiedAt = LocalDateTime.of(2026, 9, 18, 10, 30);

    VisitVerificationView.ArtistView artist =
        VisitVerificationView.ArtistView.builder()
            .artistId(10L)
            .artistName("유재석")
            .build();

    VisitVerificationView view =
        VisitVerificationView.builder()
            .visitVerificationId(1L)
            .locationId(100L)
            .locationName("해운대해수욕장")
            .locationAddress("부산광역시 해운대구")
            .locationImageUrl("https://example.com/location.jpg")
            .city("부산광역시")
            .contentId(200L)
            .contentTitle("런닝맨")
            .artists(List.of(artist))
            .visitVerifiedDate(verifiedDate)
            .visitVerifiedTimeAt(verifiedAt)
            .visitVerificationStatus(VisitVerificationStatus.VALID)
            .liked(true)
            .archived(true)
            .build();

    VisitVerificationHistoriesIndividualResult result =
        VisitVerificationHistoriesIndividualResult.from(view);

    assertThat(result.visitVerificationId()).isEqualTo(1L);

    assertThat(result.locationId()).isEqualTo(100L);
    assertThat(result.locationName()).isEqualTo("해운대해수욕장");
    assertThat(result.locationAddress()).isEqualTo("부산광역시 해운대구");
    assertThat(result.locationImageUrl())
        .isEqualTo("https://example.com/location.jpg");
    assertThat(result.city()).isEqualTo("부산광역시");

    assertThat(result.contentId()).isEqualTo(200L);
    assertThat(result.contentTitle()).isEqualTo("런닝맨");

    assertThat(result.artists()).hasSize(1);

    assertThat(result.artists().getFirst().artistId())
        .isEqualTo(10L);

    assertThat(result.artists().getFirst().artistName())
        .isEqualTo("유재석");

    assertThat(result.visitVerifiedDate())
        .isEqualTo(verifiedDate);

    assertThat(result.visitVerifiedTimeAt())
        .isEqualTo(verifiedAt);

    assertThat(result.visitVerificationStatus())
        .isEqualTo(VisitVerificationStatus.VALID.name());

    assertThat(result.liked()).isTrue();
    assertThat(result.archived()).isTrue();
  }

  @Test
  @DisplayName("좋아요와 보관하지 않은 상태도 Result에 반영한다")
  void from_notLikedAndNotArchived() {

    VisitVerificationView view =
        VisitVerificationView.builder()
            .visitVerificationId(1L)
            .locationId(100L)
            .locationName("광안리")
            .locationAddress("부산광역시 수영구")
            .locationImageUrl("image")
            .city("부산광역시")
            .contentId(200L)
            .contentTitle("테스트 콘텐츠")
            .artists(List.of())
            .visitVerifiedDate(LocalDate.of(2026, 9, 18))
            .visitVerifiedTimeAt(
                LocalDateTime.of(2026, 9, 18, 12, 0))
            .visitVerificationStatus(
                VisitVerificationStatus.VALID)
            .liked(false)
            .archived(false)
            .build();

    VisitVerificationHistoriesIndividualResult result =
        VisitVerificationHistoriesIndividualResult.from(view);

    assertThat(result.liked()).isFalse();
    assertThat(result.archived()).isFalse();
    assertThat(result.artists()).isEmpty();
  }

  @Test
  @DisplayName("여러 아티스트 정보를 모두 Result로 변환한다")
  void from_multipleArtists() {

    VisitVerificationView.ArtistView artist1 =
        VisitVerificationView.ArtistView.builder()
            .artistId(1L)
            .artistName("ARTIST_1")
            .build();

    VisitVerificationView.ArtistView artist2 =
        VisitVerificationView.ArtistView.builder()
            .artistId(2L)
            .artistName("ARTIST_2")
            .build();

    VisitVerificationView view =
        VisitVerificationView.builder()
            .visitVerificationId(10L)
            .locationId(20L)
            .locationName("LOCATION")
            .locationAddress("ADDRESS")
            .locationImageUrl("IMAGE")
            .city("부산광역시")
            .contentId(30L)
            .contentTitle("CONTENT")
            .artists(List.of(artist1, artist2))
            .visitVerifiedDate(LocalDate.of(2026, 9, 18))
            .visitVerifiedTimeAt(
                LocalDateTime.of(2026, 9, 18, 15, 0))
            .visitVerificationStatus(
                VisitVerificationStatus.VALID)
            .liked(true)
            .archived(false)
            .build();

    VisitVerificationHistoriesIndividualResult result =
        VisitVerificationHistoriesIndividualResult.from(view);

    assertThat(result.artists()).hasSize(2);

    assertThat(result.artists().get(0).artistId())
        .isEqualTo(1L);

    assertThat(result.artists().get(0).artistName())
        .isEqualTo("ARTIST_1");

    assertThat(result.artists().get(1).artistId())
        .isEqualTo(2L);

    assertThat(result.artists().get(1).artistName())
        .isEqualTo("ARTIST_2");
  }
}