package com.tracek.domain.location.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.location.application.dto.LocationDetailResult;
import com.tracek.domain.location.application.dto.LocationNearbyResult;
import com.tracek.domain.location.application.dto.LocationRelatedInfoResult;
import com.tracek.domain.location.application.facade.LocationFacade;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.domain.location.presentation.request.LocationNearbyRequest;
import com.tracek.domain.location.presentation.response.LocationDetailResponse;
import com.tracek.domain.location.presentation.response.LocationNearbyResponse;
import com.tracek.domain.location.presentation.response.LocationRelatedInfoResponse;
import com.tracek.global.response.ApiResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocationQueryControllerTest {

    @Mock private LocationFacade locationFacade;
    @Mock private LocationQueryService locationQueryService;

    private LocationQueryController controller;

    @BeforeEach
    void setUp() {
        controller = new LocationQueryController(locationFacade, locationQueryService);
    }

    @Test
    @DisplayName("관광지 단건 상세 조회 성공 시 성공 응답으로 감싸서 반환한다")
    void getLocationDetails_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        LocationDetailResult result =
                LocationDetailResult.from(
                        LocationDetailResult.LocationInfo.of(location),
                        Collections.emptyList(),
                        Collections.emptyList());
        given(locationFacade.getLocationDetails(1L)).willReturn(result);

        ApiResponse<LocationDetailResponse> response = controller.getLocationDetails(1L);

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData().getLocationInfo().getLocationId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("지도 범위 내 관광지 리스트를 응답으로 감싸서 반환한다")
    void getNearbyLocations_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(locationQueryService.getNearbyLocations(37.5, 127.0, 1000.0))
                .willReturn(List.of(LocationNearbyResult.of(location, 100.0)));

        LocationNearbyRequest request = LocationNearbyRequest.of(37.5, 127.0, 1000.0);
        ApiResponse<List<LocationNearbyResponse>> response = controller.getNearbyLocations(request);

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().get(0).getLocationId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("관광지 연관 콘텐츠-아티스트 조회 결과를 응답으로 감싸서 반환한다")
    void getRelatedContentAndArtists_success() {
        LocationRelatedInfoResult result =
                LocationRelatedInfoResult.of(1L, "경복궁", Collections.emptyList());
        given(locationFacade.getRelatedContentAndArtists(1L)).willReturn(result);

        ApiResponse<LocationRelatedInfoResponse> response =
                controller.getRelatedContentAndArtists(1L);

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData().getLocationId()).isEqualTo(1L);
    }
}
