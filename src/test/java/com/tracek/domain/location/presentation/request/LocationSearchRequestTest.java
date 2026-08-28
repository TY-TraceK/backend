package com.tracek.domain.location.presentation.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.location.application.dto.LocationSearchQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationSearchRequestTest {

    @Test
    @DisplayName("size를 지정하지 않으면 기본값 20이 적용된다")
    void toQuery_defaultsSizeWhenNull() {
        LocationSearchRequest request = new LocationSearchRequest("경복궁", "ATTRACTION", 10L, null);

        LocationSearchQuery query = request.toQuery();

        assertThat(query.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("size가 1 미만이면 기본값 20이 적용된다")
    void toQuery_defaultsSizeWhenLessThanOne() {
        LocationSearchRequest request = new LocationSearchRequest("경복궁", "ATTRACTION", 10L, 0);

        LocationSearchQuery query = request.toQuery();

        assertThat(query.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("keyword/category/lastLocationId/size를 그대로 Query에 전달한다")
    void toQuery_passesFieldsThrough() {
        LocationSearchRequest request = new LocationSearchRequest("경복궁", "ATTRACTION", 10L, 5);

        LocationSearchQuery query = request.toQuery();

        assertThat(query.getKeyword()).isEqualTo("경복궁");
        assertThat(query.getCategory()).isEqualTo("ATTRACTION");
        assertThat(query.getLastLocationId()).isEqualTo(10L);
        assertThat(query.getSize()).isEqualTo(5);
    }
}
