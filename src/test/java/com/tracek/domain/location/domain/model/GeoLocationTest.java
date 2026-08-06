package com.tracek.domain.location.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GeoLocationTest {

    @Test
    @DisplayName("유효한 위경도 범위면 GeoLocation을 생성한다")
    void of_valid() {
        GeoLocation geoLocation = GeoLocation.of(37.5665, 126.9780);

        assertThat(geoLocation.getLatitude()).isEqualTo(37.5665);
        assertThat(geoLocation.getLongitude()).isEqualTo(126.9780);
    }

    @Test
    @DisplayName("위도가 범위를 벗어나면 예외가 발생한다")
    void of_invalidLatitude() {
        assertThatThrownBy(() -> GeoLocation.of(91, 126.9780))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.INVALID_GEO_LOCATION);
    }

    @Test
    @DisplayName("경도가 범위를 벗어나면 예외가 발생한다")
    void of_invalidLongitude() {
        assertThatThrownBy(() -> GeoLocation.of(37.5665, 181))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.INVALID_GEO_LOCATION);
    }

    @Test
    @DisplayName("같은 좌표 간 거리는 0m에 가깝다")
    void calculateDistanceMeterTo_samePoint() {
        GeoLocation location = GeoLocation.of(37.5665, 126.9780);

        double distance = location.calculateDistanceMeterTo(location);

        assertThat(distance).isCloseTo(0.0, within(0.001));
    }

    @Test
    @DisplayName("서울시청과 부산시청 간 거리는 약 325km이다")
    void calculateDistanceMeterTo_seoulToBusan() {
        GeoLocation seoul = GeoLocation.of(37.5665, 126.9780);
        GeoLocation busan = GeoLocation.of(35.1796, 129.0756);

        double distanceMeter = seoul.calculateDistanceMeterTo(busan);

        assertThat(distanceMeter).isCloseTo(325_000, within(5_000.0));
    }
}
