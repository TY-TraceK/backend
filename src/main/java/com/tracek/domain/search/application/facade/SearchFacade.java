package com.tracek.domain.search.application.facade;

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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SearchFacade {
    private final ArtistSearchQueryService artistSearchQueryService;
    private final LocationSearchQueryService locationSearchQueryService;
    private final ContentSearchQueryService contentSearchQueryService;

    public UnifiedSearchResult search(String keyword) {
        ArtistSearchResult artists =
                artistSearchQueryService.searchArtists(ArtistSearchQuery.of(keyword, null, 20));
        ContentSearchResult contents =
                contentSearchQueryService.searchContents(ContentSearchQuery.of(keyword, null, 20));
        LocationSearchResult locations =
                locationSearchQueryService.searchLocationsByName(
                        LocationSearchQuery.of(keyword, null, 20));

        return UnifiedSearchResult.of(
                artists.getArtists(), contents.getContents(), locations.getLocations());
    }
}
