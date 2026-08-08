package com.tracek.domain.location.presentation.response;

import com.tracek.domain.location.application.dto.LocationDetailResult;
import com.tracek.domain.location.domain.model.Address;
import com.tracek.domain.location.domain.model.GeoLocation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationDetailResponse {

    private LocationInfoResponse locationInfo;
    private List<LocationImageResponse> images;
    private List<LocationContentResponse> contents;

    public static LocationDetailResponse from(LocationDetailResult result) {
        List<LocationImageResponse> imageResponses =
                result.getImages().stream().map(LocationImageResponse::from).toList();
        List<LocationContentResponse> contentResponses =
                result.getContents().stream().map(LocationContentResponse::from).toList();

        return new LocationDetailResponse(
                LocationInfoResponse.from(result.getLocationInfo()), imageResponses, contentResponses);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationInfoResponse {
        private Long locationId;
        private String name;
        private String category;
        private Long likeCount;
        private Address address;
        private GeoLocation geoLocation;
        private String mainImageUrl;

        public static LocationInfoResponse from(LocationDetailResult.LocationInfo locationInfo) {
            return new LocationInfoResponse(
                    locationInfo.getId(),
                    locationInfo.getName(),
                    locationInfo.getCategory(),
                    locationInfo.getLikeCount(),
                    locationInfo.getAddress(),
                    locationInfo.getGeoLocation(),
                    locationInfo.getMainImageUrl());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationImageResponse {
        private Long imageId;
        private String imageUrl;
        private Boolean isMain;
        private Integer displayOrder;

        public static LocationImageResponse from(LocationDetailResult.LocationImageResult imageResult) {
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
        private List<LocationArtistResponse> artists;

        public static LocationContentResponse from(LocationDetailResult.ContentResult contentResult) {
            List<LocationArtistResponse> artistResponses =
                    contentResult.getArtists().stream().map(LocationArtistResponse::from).toList();

            return new LocationContentResponse(
                    contentResult.getContentId(),
                    contentResult.getContentTitle(),
                    contentResult.getContentType(),
                    contentResult.getContentImageUrl(),
                    artistResponses);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationArtistResponse {
        private Long artistId;
        private String artistName;
        private String artistPictureUrl;

        public static LocationArtistResponse from(LocationDetailResult.ArtistResult artistResult) {
            return new LocationArtistResponse(
                    artistResult.getArtistId(),
                    artistResult.getArtistName(),
                    artistResult.getArtistPictureUrl());
        }
    }
}
