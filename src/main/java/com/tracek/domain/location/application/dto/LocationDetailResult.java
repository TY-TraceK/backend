package com.tracek.domain.location.application.dto;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.location.domain.model.Address;
import com.tracek.domain.location.domain.model.GeoLocation;
import com.tracek.domain.location.domain.model.Location;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocationDetailResult {
    private LocationInfo locationInfo;
    private List<LocationImageResult> images;
    private List<ContentResult> contents;

    public static LocationDetailResult from(LocationInfo locationInfo, List<LocationImageResult> images, List<ContentResult> contents) {
        return new LocationDetailResult(locationInfo, images, contents);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LocationInfo{
        private Long id;
        private String name;
        private String category;
        private Long likeCount;
        private Address address;
        private GeoLocation geoLocation;
        private String mainImageUrl;

        public static LocationInfo of(Location location) {
            return new LocationInfo(
                    location.getId(),
                    location.getName(),
                    location.getCategory(),
                    location.getLikeCount(),
                    location.getAddress(),
                    location.getGeoLocation(),
                    location.getMainImageUrl().getImageUrl()
            );
        }

    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LocationImageResult{
        private Long imageId;
        private String imageUrl;
        private Boolean isMain;
        private Integer displayOrder;

        public static LocationImageResult of(
                ImageResult imageResult, Boolean isMain, Integer displayOrder) {
            return new LocationImageResult(
                    imageResult.getId(), imageResult.getImageUrl(), isMain, displayOrder);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ContentResult{
        private Long contentId;
        private String contentTitle;
        private String contentType;
        private String contentImageUrl;
        private List<ArtistResult> artists;

        public static  ContentResult of(com.tracek.domain.content.application.dto.ContentResult contentResult, List<ArtistResult> artists) {
            return new ContentResult(
                    contentResult.getContentId(),
                    contentResult.getTitle(),
                    contentResult.getCategory(),
                    contentResult.getPictureUrl(),
                    artists
            );
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ArtistResult{
        private Long artistId;
        private String artistName;
        private String artistPictureUrl;

        public static ArtistResult from(com.tracek.domain.artist.application.dto.ArtistResult artist) {
            return new ArtistResult(artist.getId(), artist.getName(), artist.getPictureUrl());
        }
    }
}
