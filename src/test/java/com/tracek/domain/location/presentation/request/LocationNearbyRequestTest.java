package com.tracek.domain.location.presentation.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationNearbyRequestTest {

    @Test
    @DisplayName("반경을 지정하지 않으면 기본값 1000m가 적용된다")
    void getRadiusMeter_defaultsWhenNull() {
        LocationNearbyRequest request = LocationNearbyRequest.of(37.5, 127.0, null);

        assertThat(request.getRadiusMeter()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("반경을 지정하면 지정한 값을 그대로 사용한다")
    void getRadiusMeter_usesGivenValue() {
        LocationNearbyRequest request = LocationNearbyRequest.of(37.5, 127.0, 500.0);

        assertThat(request.getRadiusMeter()).isEqualTo(500.0);
    }
}
