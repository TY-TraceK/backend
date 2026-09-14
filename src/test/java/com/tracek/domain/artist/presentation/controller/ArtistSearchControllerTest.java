package com.tracek.domain.artist.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.application.dto.ArtistSearchQuery;
import com.tracek.domain.artist.application.dto.ArtistSearchResult;
import com.tracek.domain.artist.application.service.ArtistSearchQueryService;
import com.tracek.domain.artist.presentation.request.ArtistSearchRequest;
import com.tracek.domain.artist.presentation.response.ArtistSearchResponse;
import com.tracek.global.response.ApiResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArtistSearchControllerTest {

    @Mock private ArtistSearchQueryService artistSearchQueryService;

    private ArtistSearchController controller;

    @BeforeEach
    void setUp() {
        controller = new ArtistSearchController(artistSearchQueryService);
    }

    @Test
    @DisplayName("검색 결과를 성공 응답으로 감싸서 반환한다")
    void searchArtists_success() {
        ArtistSearchRequest request = new ArtistSearchRequest("아이유", null, 20);
        ArtistSearchResult.ArtistInfo info =
                new ArtistSearchResult.ArtistInfo(
                        1L, "아이유", "IU", "http://image.com/iu.jpg", null, false);
        ArtistSearchResult result = ArtistSearchResult.of(List.of(info), 20);
        given(artistSearchQueryService.searchArtists(any(ArtistSearchQuery.class)))
                .willReturn(result);

        ApiResponse<ArtistSearchResponse> response = controller.searchArtists(request);

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData().getArtists()).hasSize(1);
        assertThat(response.getData().getArtists().get(0).getName()).isEqualTo("아이유");
    }
}
