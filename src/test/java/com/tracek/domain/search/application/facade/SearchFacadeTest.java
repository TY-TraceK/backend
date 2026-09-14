package com.tracek.domain.search.application.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.application.dto.ArtistSearchQuery;
import com.tracek.domain.artist.application.dto.ArtistSearchResult;
import com.tracek.domain.artist.application.service.ArtistSearchQueryService;
import com.tracek.domain.content.application.dto.ContentSearchQuery;
import com.tracek.domain.content.application.dto.ContentSearchResult;
import com.tracek.domain.content.application.service.ContentSearchQueryService;
import com.tracek.domain.location.application.dto.LocationSearchQuery;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import com.tracek.domain.location.application.service.LocationSearchQueryService;
import com.tracek.domain.search.application.dto.UnifiedSearchResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SearchFacadeTest {

    @Mock private ArtistSearchQueryService artistSearchQueryService;
    @Mock private LocationSearchQueryService locationSearchQueryService;
    @Mock private ContentSearchQueryService contentSearchQueryService;

    private SearchFacade searchFacade;

    @BeforeEach
    void setUp() {
        searchFacade =
                new SearchFacade(
                        artistSearchQueryService,
                        locationSearchQueryService,
                        contentSearchQueryService);
    }

    @Test
    @DisplayName("키워드로 아티스트/콘텐츠/관광지를 각각 검색해 섹션별로 조립한다")
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

        given(artistSearchQueryService.searchArtists(any(ArtistSearchQuery.class)))
                .willReturn(ArtistSearchResult.of(List.of(artistInfo), 20));
        given(contentSearchQueryService.searchContents(any(ContentSearchQuery.class)))
                .willReturn(ContentSearchResult.of(List.of(contentInfo), 20));
        given(locationSearchQueryService.searchLocationsByName(any(LocationSearchQuery.class)))
                .willReturn(LocationSearchResult.of(List.of(locationInfo), 20));

        UnifiedSearchResult result = searchFacade.search("경복궁");

        assertThat(result.getArtists()).hasSize(1);
        assertThat(result.getArtists().get(0).getName()).isEqualTo("아이유");
        assertThat(result.getContents()).hasSize(1);
        assertThat(result.getContents().get(0).getTitle()).isEqualTo("궁궐 브이로그");
        assertThat(result.getLocations()).hasSize(1);
        assertThat(result.getLocations().get(0).getName()).isEqualTo("경복궁");
    }
}
