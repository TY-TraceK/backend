package com.tracek.domain.search.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.application.dto.ArtistSearchResult;
import com.tracek.domain.content.application.dto.ContentSearchResult;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import com.tracek.domain.search.application.dto.UnifiedSearchResult;
import com.tracek.domain.search.application.facade.SearchFacade;
import com.tracek.domain.search.presentation.response.UnifiedSearchResponse;
import com.tracek.global.response.ApiResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock private SearchFacade searchFacade;

    private SearchController controller;

    @BeforeEach
    void setUp() {
        controller = new SearchController(searchFacade);
    }

    @Test
    @DisplayName("통합검색 결과를 성공 응답으로 감싸서 반환한다")
    void search_success() {
        ArtistSearchResult.ArtistInfo artistInfo =
                new ArtistSearchResult.ArtistInfo(
                        1L, "아이유", "IU", "http://image.com/iu.jpg", null, false);
        ContentSearchResult.ContentInfo contentInfo =
                new ContentSearchResult.ContentInfo(
                        2L, "궁궐 브이로그", "ENTERTAINMENT", "http://image.com/c.jpg");
        LocationSearchResult.LocationInfo locationInfo =
                new LocationSearchResult.LocationInfo(
                        3L, "경복궁", "ATTRACTION", "서울 종로구 사직로 161", "http://image.com/a.jpg");
        UnifiedSearchResult result =
                UnifiedSearchResult.of(
                        List.of(artistInfo), List.of(contentInfo), List.of(locationInfo));
        given(searchFacade.search("경복궁")).willReturn(result);

        ApiResponse<UnifiedSearchResponse> response = controller.search("경복궁");

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData().getArtists()).hasSize(1);
        assertThat(response.getData().getContents()).hasSize(1);
        assertThat(response.getData().getLocations()).hasSize(1);
    }
}
