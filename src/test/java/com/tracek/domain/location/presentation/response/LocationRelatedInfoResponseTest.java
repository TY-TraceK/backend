package com.tracek.domain.location.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.location.application.dto.LocationRelatedInfoResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationRelatedInfoResponseTest {

    @Test
    @DisplayName("LocationRelatedInfoResult를 LocationRelatedInfoResponse로 변환한다")
    void from_success() {
        LocationRelatedInfoResult.RelatedItemResult item =
                LocationRelatedInfoResult.RelatedItemResult.of(
                        2L,
                        "궁궐 브이로그",
                        "VARIETY",
                        "http://image.com/c.jpg",
                        3L,
                        "아이유",
                        "http://image.com/ar.jpg");
        LocationRelatedInfoResult result = LocationRelatedInfoResult.of(1L, "경복궁", List.of(item));

        LocationRelatedInfoResponse response = LocationRelatedInfoResponse.from(result);

        assertThat(response.getLocationId()).isEqualTo(1L);
        assertThat(response.getLocationName()).isEqualTo("경복궁");
        assertThat(response.getRelatedItems()).hasSize(1);
        assertThat(response.getRelatedItems().get(0).getContentTitle()).isEqualTo("궁궐 브이로그");
        assertThat(response.getRelatedItems().get(0).getArtistName()).isEqualTo("아이유");
    }
}
