package com.tracek.domain.location.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.location.application.dto.LocationNearbyResult;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationNearbyResponseTest {

    @Test
    @DisplayName("LocationNearbyResult를 LocationNearbyResponse로 변환한다")
    void from_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        LocationNearbyResult result = LocationNearbyResult.of(location, 123.45);

        LocationNearbyResponse response = LocationNearbyResponse.from(result);

        assertThat(response.getLocationId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("경복궁");
        assertThat(response.getCategory()).isEqualTo("ATTRACTION");
        assertThat(response.getDistanceMeter()).isEqualTo(123.45);
    }
}
