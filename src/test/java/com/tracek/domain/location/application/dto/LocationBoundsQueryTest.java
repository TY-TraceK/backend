package com.tracek.domain.location.application.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.domain.location.domain.model.LocationCategory;
import com.tracek.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationBoundsQueryTest {

    @Test
    @DisplayName("정상 범위면 그대로 생성된다")
    void of_success() {
        LocationBoundsQuery query = LocationBoundsQuery.of(35.0, 128.9, 35.2, 129.1, null);

        assertThat(query.getSwLat()).isEqualTo(35.0);
        assertThat(query.getSwLng()).isEqualTo(128.9);
        assertThat(query.getNeLat()).isEqualTo(35.2);
        assertThat(query.getNeLng()).isEqualTo(129.1);
        assertThat(query.getCategory()).isNull();
    }

    @Test
    @DisplayName("category를 지정하면 LocationCategory로 변환된다")
    void of_withCategory_convertsToEnum() {
        LocationBoundsQuery query = LocationBoundsQuery.of(35.0, 128.9, 35.2, 129.1, "ATTRACTION");

        assertThat(query.getCategory()).isEqualTo(LocationCategory.ATTRACTION);
    }

    @Test
    @DisplayName("유효하지 않은 category면 INVALID_CATEGORY 예외가 발생한다")
    void of_invalidCategory_throws() {
        assertThatThrownBy(() -> LocationBoundsQuery.of(35.0, 128.9, 35.2, 129.1, "NOT_A_CATEGORY"))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.INVALID_CATEGORY);
    }

    @Test
    @DisplayName("좌표 중 하나라도 null이면 INVALID_BOUNDS 예외가 발생한다")
    void of_nullCoordinate_throws() {
        assertThatThrownBy(() -> LocationBoundsQuery.of(null, 128.9, 35.2, 129.1, null))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.INVALID_BOUNDS);
    }

    @Test
    @DisplayName("위경도 범위를 벗어나면 INVALID_GEO_LOCATION 예외가 발생한다")
    void of_outOfRange_throws() {
        assertThatThrownBy(() -> LocationBoundsQuery.of(-95.0, 128.9, 35.2, 129.1, null))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.INVALID_GEO_LOCATION);
    }

    @Test
    @DisplayName("남서쪽 좌표가 북동쪽 좌표보다 크거나 같으면 INVALID_BOUNDS 예외가 발생한다")
    void of_swGreaterThanNe_throws() {
        assertThatThrownBy(() -> LocationBoundsQuery.of(35.2, 128.9, 35.0, 129.1, null))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.INVALID_BOUNDS);
    }

    @Test
    @DisplayName("위경도 차이가 상한을 넘으면 BOUNDS_TOO_WIDE 예외가 발생한다")
    void of_tooWide_throws() {
        assertThatThrownBy(() -> LocationBoundsQuery.of(34.0, 128.0, 35.5, 130.0, null))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.BOUNDS_TOO_WIDE);
    }

    @Test
    @DisplayName("busanDefault는 부산광역시청 기준 기본 범위를 반환한다")
    void busanDefault_success() {
        LocationBoundsQuery query = LocationBoundsQuery.busanDefault();

        assertThat(query.getSwLat()).isLessThan(query.getNeLat());
        assertThat(query.getSwLng()).isLessThan(query.getNeLng());
        assertThat(query.getCategory()).isNull();
    }

    @Test
    @DisplayName("busanDefault에 category를 지정하면 기본 범위 + 카테고리로 반환된다")
    void busanDefault_withCategory() {
        LocationBoundsQuery query = LocationBoundsQuery.busanDefault("CAFE");

        assertThat(query.getCategory()).isEqualTo(LocationCategory.CAFE);
    }
}
