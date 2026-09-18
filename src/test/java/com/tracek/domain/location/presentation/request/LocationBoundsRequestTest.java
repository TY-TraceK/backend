package com.tracek.domain.location.presentation.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.location.application.dto.LocationBoundsQuery;
import com.tracek.domain.location.domain.model.LocationCategory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationBoundsRequestTest {

    @Test
    @DisplayName("좌표를 모두 입력하지 않으면 부산광역시청 기준 기본 범위로 변환된다")
    void toQuery_allNull_usesBusanDefault() {
        LocationBoundsRequest request =
                new LocationBoundsRequest(null, null, null, null, null, false);

        LocationBoundsQuery query = request.toQuery();
        LocationBoundsQuery busanDefault = LocationBoundsQuery.busanDefault();

        assertThat(query.getSwLat()).isEqualTo(busanDefault.getSwLat());
        assertThat(query.getSwLng()).isEqualTo(busanDefault.getSwLng());
        assertThat(query.getNeLat()).isEqualTo(busanDefault.getNeLat());
        assertThat(query.getNeLng()).isEqualTo(busanDefault.getNeLng());
    }

    @Test
    @DisplayName("좌표를 입력하면 그대로 Query에 전달된다")
    void toQuery_withCoordinates_passesThrough() {
        LocationBoundsRequest request =
                new LocationBoundsRequest(35.0, 128.9, 35.2, 129.1, null, false);

        LocationBoundsQuery query = request.toQuery();

        assertThat(query.getSwLat()).isEqualTo(35.0);
        assertThat(query.getSwLng()).isEqualTo(128.9);
        assertThat(query.getNeLat()).isEqualTo(35.2);
        assertThat(query.getNeLng()).isEqualTo(129.1);
    }

    @Test
    @DisplayName("category를 입력하면 LocationCategory로 변환되어 Query에 전달된다")
    void toQuery_withCategory_convertsToEnum() {
        LocationBoundsRequest request =
                new LocationBoundsRequest(35.0, 128.9, 35.2, 129.1, "CAFE", false);

        LocationBoundsQuery query = request.toQuery();

        assertThat(query.getCategory()).isEqualTo(LocationCategory.CAFE);
    }

    @Test
    @DisplayName("좌표 없이 category만 입력하면 부산광역시청 기본 범위 + 카테고리로 변환된다")
    void toQuery_onlyCategory_usesBusanDefaultWithCategory() {
        LocationBoundsRequest request =
                new LocationBoundsRequest(null, null, null, null, "CAFE", false);

        LocationBoundsQuery query = request.toQuery();
        LocationBoundsQuery busanDefault = LocationBoundsQuery.busanDefault();

        assertThat(query.getSwLat()).isEqualTo(busanDefault.getSwLat());
        assertThat(query.getCategory()).isEqualTo(LocationCategory.CAFE);
    }

    @Test
    @DisplayName("archivedOnly를 true로 입력하면 그대로 Query에 전달된다")
    void toQuery_withArchivedOnly_passesThrough() {
        LocationBoundsRequest request =
                new LocationBoundsRequest(35.0, 128.9, 35.2, 129.1, null, true);

        LocationBoundsQuery query = request.toQuery();

        assertThat(query.isArchivedOnly()).isTrue();
    }
}
