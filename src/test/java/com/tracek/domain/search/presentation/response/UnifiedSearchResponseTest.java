package com.tracek.domain.search.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.artist.application.dto.ArtistSearchResult;
import com.tracek.domain.content.application.dto.ContentSearchResult;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import com.tracek.domain.search.application.dto.UnifiedSearchResult;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UnifiedSearchResponseTest {

    @Test
    @DisplayName("UnifiedSearchResult를 UnifiedSearchResponse로 변환하면 세 섹션이 모두 매핑된다")
    void from_success() {
        ArtistSearchResult.ArtistInfo artistInfo =
                new ArtistSearchResult.ArtistInfo(
                        1L, "아이유", "IU", "http://image.com/iu.jpg", null, false);
        ContentSearchResult.ContentInfo contentInfo =
                new ContentSearchResult.ContentInfo(
                        2L, "궁궐 브이로그", "ENTERTAINMENT", "http://image.com/c.jpg");
        LocationSearchResult.LocationInfo locationInfo =
                new LocationSearchResult.LocationInfo(
                        3L,
                        "경복궁",
                        "ATTRACTION",
                        "서울 종로구 사직로 161",
                        "http://image.com/a.jpg",
                        35.1796,
                        129.0756);

        UnifiedSearchResult result =
                UnifiedSearchResult.of(
                        List.of(artistInfo), List.of(contentInfo), List.of(locationInfo));

        UnifiedSearchResponse response = UnifiedSearchResponse.from(result);

        assertThat(response.getArtists()).hasSize(1);
        assertThat(response.getArtists().get(0).getName()).isEqualTo("아이유");
        assertThat(response.getContents()).hasSize(1);
        assertThat(response.getContents().get(0).getTitle()).isEqualTo("궁궐 브이로그");
        assertThat(response.getLocations()).hasSize(1);
        assertThat(response.getLocations().get(0).getName()).isEqualTo("경복궁");
    }

    @Test
    @DisplayName("섹션이 모두 비어있으면 빈 목록으로 변환된다")
    void from_empty() {
        UnifiedSearchResult result = UnifiedSearchResult.of(List.of(), List.of(), List.of());

        UnifiedSearchResponse response = UnifiedSearchResponse.from(result);

        assertThat(response.getArtists()).isEmpty();
        assertThat(response.getContents()).isEmpty();
        assertThat(response.getLocations()).isEmpty();
    }
}
