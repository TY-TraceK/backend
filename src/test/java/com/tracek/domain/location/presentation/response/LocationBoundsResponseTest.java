package com.tracek.domain.location.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.location.application.dto.LocationBoundsResult;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationBoundsResponseTest {

    @Test
    @DisplayName("LocationBoundsResult를 LocationBoundsResponse로 변환하면 목록이 매핑된다")
    void from_success() {
        LocationSearchResult.LocationInfo info =
                new LocationSearchResult.LocationInfo(
                        1L,
                        "경복궁",
                        "ATTRACTION",
                        "서울 종로구 사직로 161",
                        "http://image.com/a.jpg",
                        35.1796,
                        129.0756);
        LocationBoundsResult result = LocationBoundsResult.of(List.of(info));

        LocationBoundsResponse response = LocationBoundsResponse.from(result);

        assertThat(response.getLocations()).hasSize(1);
        assertThat(response.getLocations().get(0).getName()).isEqualTo("경복궁");
        assertThat(response.getLocations().get(0).getLatitude()).isEqualTo(35.1796);
        assertThat(response.getLocations().get(0).getLongitude()).isEqualTo(129.0756);
    }

    @Test
    @DisplayName("결과가 없으면 빈 목록으로 변환된다")
    void from_empty() {
        LocationBoundsResult result = LocationBoundsResult.of(List.of());

        LocationBoundsResponse response = LocationBoundsResponse.from(result);

        assertThat(response.getLocations()).isEmpty();
    }
}
