package com.tracek.domain.location.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.location.application.dto.LocationSearchResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationSearchResponseTest {

    @Test
    @DisplayName("LocationSearchResult를 LocationSearchResponse로 변환하면 목록/hasNext/lastId가 모두 매핑된다")
    void from_success() {
        LocationSearchResult.LocationInfo info =
                new LocationSearchResult.LocationInfo(
                        1L, "경복궁", "ATTRACTION", "서울 종로구 사직로 161", "http://image.com/a.jpg");
        LocationSearchResult result = LocationSearchResult.of(List.of(info), 1);

        LocationSearchResponse response = LocationSearchResponse.from(result);

        assertThat(response.getLocations()).hasSize(1);
        assertThat(response.getLocations().get(0).getId()).isEqualTo(1L);
        assertThat(response.getLocations().get(0).getName()).isEqualTo("경복궁");
        assertThat(response.getLocations().get(0).getCategory()).isEqualTo("ATTRACTION");
        assertThat(response.getLocations().get(0).getAddress()).isEqualTo("서울 종로구 사직로 161");
        assertThat(response.getLocations().get(0).getMainImageUrl())
                .isEqualTo("http://image.com/a.jpg");
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.getLastId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("결과가 없으면 빈 목록과 null lastId로 변환된다")
    void from_empty() {
        LocationSearchResult result = LocationSearchResult.of(List.of(), 0);

        LocationSearchResponse response = LocationSearchResponse.from(result);

        assertThat(response.getLocations()).isEmpty();
        assertThat(response.getLastId()).isNull();
    }
}
