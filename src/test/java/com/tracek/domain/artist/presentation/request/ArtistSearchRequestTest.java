package com.tracek.domain.artist.presentation.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.artist.application.dto.ArtistSearchQuery;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArtistSearchRequestTest {

    @Test
    @DisplayName("size를 지정하지 않으면 기본값 20이 적용된다")
    void toQuery_defaultsSizeWhenNull() {
        ArtistSearchRequest request = new ArtistSearchRequest("아이유", 10L, null);

        ArtistSearchQuery query = request.toQuery();

        assertThat(query.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("size가 1 미만이면 기본값 20이 적용된다")
    void toQuery_defaultsSizeWhenLessThanOne() {
        ArtistSearchRequest request = new ArtistSearchRequest("아이유", 10L, 0);

        ArtistSearchQuery query = request.toQuery();

        assertThat(query.getSize()).isEqualTo(20);
    }

    @Test
    @DisplayName("keyword/lastArtistId/size를 그대로 Query에 전달한다")
    void toQuery_passesFieldsThrough() {
        ArtistSearchRequest request = new ArtistSearchRequest("아이유", 10L, 5);

        ArtistSearchQuery query = request.toQuery();

        assertThat(query.getKeyword()).isEqualTo("아이유");
        assertThat(query.getLastArtistId()).isEqualTo(10L);
        assertThat(query.getSize()).isEqualTo(5);
    }
}
