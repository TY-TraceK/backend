package com.tracek.domain.content.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.content.application.dto.ContentSearchResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentSearchResponseTest {

    @Test
    @DisplayName("ContentSearchResult를 ContentSearchResponse로 변환하면 목록/hasNext/lastId가 모두 매핑된다")
    void from_success() {
        ContentSearchResult.ContentInfo info =
                new ContentSearchResult.ContentInfo(
                        1L, "궁궐 브이로그", "ENTERTAINMENT", "http://image.com/c.jpg");
        ContentSearchResult result = ContentSearchResult.of(List.of(info), 1);

        ContentSearchResponse response = ContentSearchResponse.from(result);

        assertThat(response.getContents()).hasSize(1);
        assertThat(response.getContents().get(0).getId()).isEqualTo(1L);
        assertThat(response.getContents().get(0).getTitle()).isEqualTo("궁궐 브이로그");
        assertThat(response.getContents().get(0).getCategory()).isEqualTo("ENTERTAINMENT");
        assertThat(response.getContents().get(0).getPictureUrl())
                .isEqualTo("http://image.com/c.jpg");
        assertThat(response.isHasNext()).isFalse();
        assertThat(response.getLastId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("결과가 없으면 빈 목록과 null lastId로 변환된다")
    void from_empty() {
        ContentSearchResult result = ContentSearchResult.of(List.of(), 0);

        ContentSearchResponse response = ContentSearchResponse.from(result);

        assertThat(response.getContents()).isEmpty();
        assertThat(response.getLastId()).isNull();
    }
}
