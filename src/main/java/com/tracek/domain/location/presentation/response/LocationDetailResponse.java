package com.tracek.domain.location.presentation.response;

import com.tracek.domain.location.application.dto.LocationDetailResult;
import com.tracek.domain.location.domain.model.Address;
import com.tracek.domain.location.domain.model.GeoLocation;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationDetailResponse {

    private LocationInfoResponse locationInfo;
    private List<LocationImageResponse> images;
    private List<LocationContentResponse> contents;
    private List<LocationArtistResponse> artists;


    public static LocationDetailResponse from(LocationDetailResult result) {
        List<LocationImageResponse> imageResponses =
                result.getImages().stream().map(LocationImageResponse::from).toList();
        List<LocationContentResponse> contentResponses =
                result.getContents().stream().map(LocationContentResponse::from).toList();
        List<LocationArtistResponse> artistResponses =
                result.getArtists().stream().map(LocationArtistResponse::from).toList();

        return new LocationDetailResponse(
                LocationInfoResponse.from(result.getLocationInfo()),
                imageResponses,
                contentResponses,
                artistResponses);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationInfoResponse {
        private Long locationId;
        private String name;
        private String category;
        private Address address;
        private GeoLocation geoLocation;
        private String mainImageUrl;
        private String tel;
        private String businessHours;
        private String overview;
        private Long archiveCount;
        private Long likeCount;
        private Long totalVerificationCount;

        public static LocationInfoResponse from(LocationDetailResult.LocationInfo locationInfo) {
            return new LocationInfoResponse(
                    locationInfo.getId(),
                    locationInfo.getName(),
                    locationInfo.getCategory(),
                    locationInfo.getAddress(),
                    locationInfo.getGeoLocation(),
                    locationInfo.getMainImageUrl(),
                    locationInfo.getTel(),
                    locationInfo.getBusinessHours(),
                    locationInfo.getOverview(),
                    locationInfo.getArchiveCount(),
                    locationInfo.getLikeCount(),
                    locationInfo.getTotalVerificationCount());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationImageResponse {
        private Long imageId;
        private String imageUrl;
        private Boolean isMain;
        private Integer displayOrder;

        public static LocationImageResponse from(
                LocationDetailResult.LocationImageResult imageResult) {
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
        private Long relatedVerificationsCount;

        public static LocationContentResponse from(LocationDetailResult.ContentResult contentResult) {
            return new LocationContentResponse(
                    contentResult.getContentId(),
                    contentResult.getContentTitle(),
                    contentResult.getContentType(),
                    contentResult.getContentImageUrl(),
                    contentResult.getRelatedVerificationsCount()
            );
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationArtistResponse {
        private Long artistId;
        private String artistName;
        private String artistPictureUrl;
        private Boolean isGroup;
        private Long relatedVerificationsCount;

        public static LocationArtistResponse from(LocationDetailResult.ArtistResult artistResult) {
            return new LocationArtistResponse(
                    artistResult.getArtistId(),
                    artistResult.getArtistName(),
                    artistResult.getArtistPictureUrl(),
                    artistResult.getIsGroup(),
                    artistResult.getRelatedVerificationsCount()
                    );
        }
    }
}
