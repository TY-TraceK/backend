package com.tracek.domain.content.application.dto;

import com.tracek.domain.content.domain.model.Content;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentDetailResult {
    private ContentInfo contentInfo;
    private List<LocationResult> locations;

    public static ContentDetailResult from(ContentInfo contentInfo, List<LocationResult> locations) {
        return new ContentDetailResult(contentInfo, locations);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ContentInfo {
        private Long id;
        private String title;
        private String category;
        private String pictureUrl;

        public static ContentInfo of(Content content) {
            return new ContentInfo(
                    content.getId(),
                    content.getTitle(),
                    content.getCategory(),
                    content.getPictureUrl().getImageUrl());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LocationResult {
        private Long locationId;
        private String locationName;
        private String locationCategory;
        private String locationPictureUrl;
        private List<ArtistResult> artists;

        public static LocationResult of(
                com.tracek.domain.location.application.dto.LocationResult locationResult,
                List<ArtistResult> artists) {
            return new LocationResult(
                    locationResult.getLocationId(),
                    locationResult.getName(),
                    locationResult.getCategory(),
                    locationResult.getMainImageUrl(),
                    artists);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ArtistResult {
        private Long artistId;
        private String artistName;
        private String artistPictureUrl;

        public static ArtistResult from(com.tracek.domain.artist.application.dto.ArtistResult artist) {
            return new ArtistResult(artist.getId(), artist.getName(), artist.getPictureUrl());
        }
    }
}
