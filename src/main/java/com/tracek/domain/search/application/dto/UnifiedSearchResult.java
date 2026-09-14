package com.tracek.domain.search.application.dto;

import com.tracek.domain.artist.application.dto.ArtistSearchResult;
import com.tracek.domain.content.application.dto.ContentSearchResult;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UnifiedSearchResult {
    private List<ArtistSearchResult.ArtistInfo> artists;
    private List<ContentSearchResult.ContentInfo> contents;
    private List<LocationSearchResult.LocationInfo> locations;

    public static UnifiedSearchResult of(
            List<ArtistSearchResult.ArtistInfo> artists,
            List<ContentSearchResult.ContentInfo> contents,
            List<LocationSearchResult.LocationInfo> locations) {
        return new UnifiedSearchResult(artists, contents, locations);
    }
}
