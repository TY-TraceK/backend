package com.tracek.domain.content.presentation.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.content.application.dto.ContentSearchQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentSearchRequestTest {

    @Test
    @DisplayName("size를 지정하지 않으면 기본값 20이 적용된다")
    void toQuery_defaultsSizeWhenNull() {
        ContentSearchRequest request = new ContentSearchRequest("궁궐 브이로그", 10L, null);

        ContentSearchQuery query = request.toQuery();

        assertThat(query.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("size가 1 미만이면 기본값 20이 적용된다")
    void toQuery_defaultsSizeWhenLessThanOne() {
        ContentSearchRequest request = new ContentSearchRequest("궁궐 브이로그", 10L, 0);

        ContentSearchQuery query = request.toQuery();

        assertThat(query.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("keyword/lastContentId/size를 그대로 Query에 전달한다")
    void toQuery_passesFieldsThrough() {
        ContentSearchRequest request = new ContentSearchRequest("궁궐 브이로그", 10L, 5);

        ContentSearchQuery query = request.toQuery();

        assertThat(query.getKeyword()).isEqualTo("궁궐 브이로그");
        assertThat(query.getLastContentId()).isEqualTo(10L);
        assertThat(query.getSize()).isEqualTo(5);
    }
}
