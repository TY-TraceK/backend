package com.tracek.domain.location.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.location.application.dto.LocationSearchQuery;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import com.tracek.domain.location.application.service.LocationSearchQueryService;
import com.tracek.domain.location.presentation.request.LocationSearchRequest;
import com.tracek.domain.location.presentation.response.LocationSearchResponse;
import com.tracek.global.response.ApiResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocationSearchControllerTest {

    @Mock private LocationSearchQueryService locationSearchQueryService;

    private LocationSearchController controller;

    @BeforeEach
    void setUp() {
        controller = new LocationSearchController(locationSearchQueryService);
    }

    @Test
    @DisplayName("검색 결과를 성공 응답으로 감싸서 반환한다")
    void searchLocations_success() {
        LocationSearchRequest request = new LocationSearchRequest("경복궁", null, null, 20);
        LocationSearchResult.LocationInfo info =
                new LocationSearchResult.LocationInfo(
                        1L, "경복궁", "ATTRACTION", "서울 종로구 사직로 161", "http://image.com/a.jpg");
        LocationSearchResult result = LocationSearchResult.of(List.of(info), 20);
        given(locationSearchQueryService.searchLocations(any(LocationSearchQuery.class)))
                .willReturn(result);

        ApiResponse<LocationSearchResponse> response = controller.searchLocations(request);

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData().getLocations()).hasSize(1);
        assertThat(response.getData().getLocations().get(0).getName()).isEqualTo("경복궁");
    }
}
