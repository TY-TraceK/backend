package com.tracek.domain.artist.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.artist.application.dto.ArtistSearchResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ArtistSearchResponseTest {

    @Test
    @DisplayName("ArtistSearchResult를 ArtistSearchResponse로 변환하면 목록/hasNext/lastId가 모두 매핑된다")
    void from_success() {
        ArtistSearchResult.ArtistInfo info =
                new ArtistSearchResult.ArtistInfo(
                        1L, "아이유", "IU", "http://image.com/iu.jpg", null, false);
        ArtistSearchResult result = ArtistSearchResult.of(List.of(info), 1);

        ArtistSearchResponse response = ArtistSearchResponse.from(result);

        assertThat(response.getArtists()).hasSize(1);
        assertThat(response.getArtists().get(0).getId()).isEqualTo(1L);
        assertThat(response.getArtists().get(0).getName()).isEqualTo("아이유");
        assertThat(response.getArtists().get(0).getAlias()).isEqualTo("IU");
        assertThat(response.getArtists().get(0).getPictureUrl())
                .isEqualTo("http://image.com/iu.jpg");
        assertThat(response.getArtists().get(0).getIsGroup()).isFalse();
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.getLastId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("결과가 없으면 빈 목록과 null lastId로 변환된다")
    void from_empty() {
        ArtistSearchResult result = ArtistSearchResult.of(List.of(), 0);

        ArtistSearchResponse response = ArtistSearchResponse.from(result);

        assertThat(response.getArtists()).isEmpty();
        assertThat(response.getLastId()).isNull();
    }
}
