package com.tracek.domain.location.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tracek.domain.location.application.LocationQueryRepository;
import com.tracek.domain.location.application.dto.LocationBoundsQuery;
import com.tracek.domain.location.application.dto.LocationBoundsResult;
import com.tracek.domain.location.application.dto.LocationSearchQuery;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocationSearchQueryServiceTest {

    @Mock private LocationQueryRepository locationQueryRepository;

    private LocationSearchQueryService service;

    @BeforeEach
    void setUp() {
        service = new LocationSearchQueryService(locationQueryRepository);
    }

    private LocationSearchResult.LocationInfo info(long id) {
        return new LocationSearchResult.LocationInfo(
                id,
                "경복궁",
                "ATTRACTION",
                "서울 종로구 사직로 161",
                "http://image.com/a.jpg",
                35.1796,
                129.0756);
    }

    @Test
    @DisplayName("keyword가 없으면 빈 결과를 즉시 반환하고 리포지토리는 호출하지 않는다")
    void searchLocations_blankKeyword_returnsEmptyWithoutCallingRepository() {
        LocationSearchQuery query = LocationSearchQuery.of(" ", null, 20);

        LocationSearchResult result = service.searchLocations(query);

        assertThat(result.getLocations()).isEmpty();
        verify(locationQueryRepository, never()).searchLocations(any(), anyInt());
    }

    @Test
    @DisplayName("결과가 요청 size보다 많으면 hasNext가 true이고 size만큼만 반환한다")
    void searchLocations_hasNext_whenMoreThanRequestedSize() {
        LocationSearchQuery query = LocationSearchQuery.of("경복궁", null, 1);
        given(locationQueryRepository.searchLocations(query, 2))
                .willReturn(List.of(info(2L), info(1L)));

        LocationSearchResult result = service.searchLocations(query);

        assertThat(result.getLocations()).hasSize(1);
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getLastId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("이름 검색 - keyword가 없으면 빈 결과를 즉시 반환하고 리포지토리는 호출하지 않는다")
    void searchLocationsByName_blankKeyword_returnsEmptyWithoutCallingRepository() {
        LocationSearchQuery query = LocationSearchQuery.of(" ", null, 20);

        LocationSearchResult result = service.searchLocationsByName(query);

        assertThat(result.getLocations()).isEmpty();
        verify(locationQueryRepository, never()).searchLocationsByName(any(), anyInt());
    }

    @Test
    @DisplayName("이름 검색 - 결과가 요청 size보다 많으면 hasNext가 true이고 size만큼만 반환한다")
    void searchLocationsByName_hasNext_whenMoreThanRequestedSize() {
        LocationSearchQuery query = LocationSearchQuery.of("경복궁", null, 1);
        given(locationQueryRepository.searchLocationsByName(query, 2))
                .willReturn(List.of(info(2L), info(1L)));

        LocationSearchResult result = service.searchLocationsByName(query);

        assertThat(result.getLocations()).hasSize(1);
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getLastId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("bounds 조회 결과를 페이징 없이 그대로 반환한다")
    void findLocationsWithinBounds_success() {
        LocationBoundsQuery query = LocationBoundsQuery.of(35.0, 128.9, 35.2, 129.1, null);
        given(locationQueryRepository.findLocationsWithinBounds(query))
                .willReturn(List.of(info(1L), info(2L)));

        LocationBoundsResult result = service.findLocationsWithinBounds(query);

        assertThat(result.getLocations()).hasSize(2);
    }
}
