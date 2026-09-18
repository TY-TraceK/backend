package com.tracek.domain.location.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.location.application.dto.LocationSimpleResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.domain.location.presentation.response.LocationSimpleResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocationMyPageControllerTest {

    @Mock private LocationQueryService locationQueryService;

    private LocationMyPageController controller;

    @Test
    @DisplayName("로그인한 유저가 좋아요한 관광지 목록을 응답으로 감싸서 반환한다")
    void getLikedLocations_success() {
        controller = new LocationMyPageController(locationQueryService);
        AuthenticationPrincipal principal = new AuthenticationPrincipal(7L, "user", "ROLE_USER");
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(locationQueryService.getLikedLocations(7L))
                .willReturn(List.of(LocationSimpleResult.from(location)));

        ApiResponse<List<LocationSimpleResponse>> response =
                controller.getLikedLocations(principal);

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().get(0).getName()).isEqualTo("경복궁");
    }

    @Test
    @DisplayName("로그인한 유저가 북마크한 관광지 목록을 응답으로 감싸서 반환한다")
    void getArchivedLocations_success() {
        controller = new LocationMyPageController(locationQueryService);
        AuthenticationPrincipal principal = new AuthenticationPrincipal(7L, "user", "ROLE_USER");
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(locationQueryService.getArchivedLocations(7L))
                .willReturn(List.of(LocationSimpleResult.from(location)));

        ApiResponse<List<LocationSimpleResponse>> response =
                controller.getArchivedLocations(principal);

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData()).hasSize(1);
        assertThat(response.getData().get(0).getName()).isEqualTo("경복궁");
    }
}
