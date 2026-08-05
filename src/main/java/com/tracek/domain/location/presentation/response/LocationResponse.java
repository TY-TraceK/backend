package com.tracek.domain.location.presentation.response;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.domain.model.Address;
import com.tracek.domain.location.domain.model.GeoLocation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationResponse {

    private Long locationId;
    private String name;
    private String category;
    private Long likeCount;
    private Address address;
    private GeoLocation geoLocation;
    private List<LocationImageResponse> images;
    private List<LocationContentResponse> contents;
    private List<LocationArtistResponse> artists;

    public static LocationResponse from(LocationResult result) {
        List<LocationImageResponse> imageResponses =
                result.getImages().stream()
                        .map(LocationImageResponse::from)
                        .collect(Collectors.toList());

        List<LocationContentResponse> contentResponses =
                result.getContents().stream().map(LocationContentResponse::from).toList();

        List<LocationArtistResponse> artistResponses =
                result.getArtists().stream().map(LocationArtistResponse::from).toList();

        return new LocationResponse(
                result.getLocationId(),
                result.getName(),
                result.getCategory(),
                result.getLikeCount(),
                result.getAddress(),
                result.getGeoLocation(),
                imageResponses,
                contentResponses,
                artistResponses);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationImageResponse {
        private Long imageId;
        private String imageUrl;
        private Boolean isMain;
        private Integer displayOrder;

        public static LocationImageResponse from(LocationResult.LocationImageResult imageResult) {
            return new LocationImageResponse(
                    imageResult.getImageId(),
                    imageResult.getImageUrl(),
                    imageResult.getIsMain(),
                    imageResult.getDisplayOrder());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationContentResponse {
        private Long contentId;
        private String contentTitle;
        private String contentType;
        private String contentPictureUrl;

        private static LocationContentResponse from(ContentResult contentResult) {
            return new LocationContentResponse(
                    contentResult.getId(),
                    contentResult.getTitle(),
                    contentResult.getCategory(),
                    contentResult.getPictureUrl());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationArtistResponse {
        private Long artistId;
        private String artistName;
        private String artistPictureUrl;

        private static LocationArtistResponse from(ArtistResult artistResult) {
            return new LocationArtistResponse(
                    artistResult.getId(), artistResult.getName(), artistResult.getPictureUrl());
        }
    }
}
