package com.tracek.domain.location.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tracek.domain.location.application.LocationQueryRepository;
import com.tracek.domain.location.application.dto.LocationSearchQuery;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.domain.location.domain.model.LocationCategory;
import com.tracek.global.exception.CustomException;
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
                id, "경복궁", "ATTRACTION", "서울 종로구 사직로 161", "http://image.com/a.jpg");
    }

    @Test
    @DisplayName("keyword가 없으면 빈 결과를 즉시 반환하고 리포지토리는 호출하지 않는다")
    void searchLocations_blankKeyword_returnsEmptyWithoutCallingRepository() {
        LocationSearchQuery query = LocationSearchQuery.of(" ", null, null, 20);

        LocationSearchResult result = service.searchLocations(query);

        assertThat(result.getLocations()).isEmpty();
        verify(locationQueryRepository, never()).searchLocations(any(), any(), anyInt());
    }

    @Test
    @DisplayName("결과가 요청 size보다 많으면 hasNext가 true이고 size만큼만 반환한다")
    void searchLocations_hasNext_whenMoreThanRequestedSize() {
        LocationSearchQuery query = LocationSearchQuery.of("경복궁", null, null, 1);
        given(locationQueryRepository.searchLocations(query, null, 2))
                .willReturn(List.of(info(2L), info(1L)));

        LocationSearchResult result = service.searchLocations(query);

        assertThat(result.getLocations()).hasSize(1);
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getLastId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("category를 지정하면 LocationCategory로 변환해서 리포지토리에 전달한다")
    void searchLocations_withCategory_convertsToEnum() {
        LocationSearchQuery query = LocationSearchQuery.of("경복궁", "ATTRACTION", null, 20);
        given(locationQueryRepository.searchLocations(query, LocationCategory.ATTRACTION, 21))
                .willReturn(List.of(info(1L)));

        LocationSearchResult result = service.searchLocations(query);

        assertThat(result.getLocations()).hasSize(1);
        verify(locationQueryRepository).searchLocations(query, LocationCategory.ATTRACTION, 21);
    }

    @Test
    @DisplayName("유효하지 않은 category면 INVALID_CATEGORY 예외가 발생한다")
    void searchLocations_invalidCategory_throws() {
        LocationSearchQuery query = LocationSearchQuery.of("경복궁", "NOT_A_CATEGORY", null, 20);

        assertThatThrownBy(() -> service.searchLocations(query))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.INVALID_CATEGORY);
    }
}
