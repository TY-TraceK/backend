package com.tracek.domain.search.presentation.response;

import com.tracek.domain.artist.application.dto.ArtistSearchResult;
import com.tracek.domain.content.application.dto.ContentSearchResult;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import com.tracek.domain.search.application.dto.UnifiedSearchResult;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UnifiedSearchResponse {
    private List<ArtistElement> artists;
    private List<ContentElement> contents;
    private List<LocationElement> locations;

    public static UnifiedSearchResponse from(UnifiedSearchResult result) {
        List<ArtistElement> artists =
                result.getArtists().stream().map(ArtistElement::from).toList();
        List<ContentElement> contents =
                result.getContents().stream().map(ContentElement::from).toList();
        List<LocationElement> locations =
                result.getLocations().stream().map(LocationElement::from).toList();

        return new UnifiedSearchResponse(artists, contents, locations);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ArtistElement {
        private Long id;
        private String name;
        private String alias;
        private String pictureUrl;
        private Long groupId;
        private Boolean isGroup;

        public static ArtistElement from(ArtistSearchResult.ArtistInfo info) {
            return new ArtistElement(
                    info.getId(),
                    info.getName(),
                    info.getAlias(),
                    info.getPictureUrl(),
                    info.getGroupId(),
                    info.getIsGroup());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ContentElement {
        private Long id;
        private String title;
        private String category;
        private String pictureUrl;

        public static ContentElement from(ContentSearchResult.ContentInfo info) {
            return new ContentElement(
                    info.getId(), info.getTitle(), info.getCategory(), info.getPictureUrl());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationElement {
        private Long id;
        private String name;
        private String category;
        private String address;
        private String mainImageUrl;

        public static LocationElement from(LocationSearchResult.LocationInfo info) {
            return new LocationElement(
                    info.getId(),
                    info.getName(),
                    info.getCategory(),
                    info.getAddress(),
                    info.getMainImageUrl());
        }
    }
}
