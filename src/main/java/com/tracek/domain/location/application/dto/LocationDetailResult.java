package com.tracek.domain.location.application.dto;

import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.location.domain.model.Address;
import com.tracek.domain.location.domain.model.GeoLocation;
import com.tracek.domain.location.domain.model.Location;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocationDetailResult {
    private LocationInfo locationInfo;
    private List<LocationImageResult> images;
    private List<ContentResult> contents;
    private List<ArtistResult> artists;

    public static LocationDetailResult of(
            LocationInfo locationInfo,
            List<LocationImageResult> images,
            List<ContentResult> contents,
            List<ArtistResult> artists) {
        return new LocationDetailResult(locationInfo, images, contents, artists);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LocationInfo {
        private Long id;
        private String name;
        private String category;
        private Address address;
        private GeoLocation geoLocation;
        private String mainImageUrl;
        private String tel;
        private String businessHours;
        private String overview;
        private Long externalContentId;
        private String sourceType;
        private Long archiveCount;
        private Long likeCount;
        private Long totalVerificationCount;

        public static LocationInfo from(Location location) {
            return new LocationInfo(
                    location.getId(),
                    location.getName(),
                    location.getCategory() == null ? null : location.getCategory().name(),
                    location.getAddress(),
                    location.getGeoLocation(),
                    location.getMainImageUrl().getImageUrl(),
                    location.getTel(),
                    location.getBusinessHours(),
                    location.getOverview(),
                    location.getExternalContentId(),
                    location.getSourceType(),
                    location.getArchiveCount(),
                    location.getLikeCount(),
                    location.getTotalVerificationCount());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LocationImageResult {
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
    public static class ContentResult {
        private Long contentId;
        private String contentTitle;
        private String contentType;
        private String contentImageUrl;
        private Long relatedVerificationsCount;

        public static ContentResult from(
                com.tracek.domain.content.application.dto.ContentResult contentResult) {
            return new ContentResult(
                    contentResult.getContentId(),
                    contentResult.getTitle(),
                    contentResult.getCategory(),
                    contentResult.getPictureUrl(),
                    null);
        }

        public static ContentResult of(
                com.tracek.domain.content.application.dto.ContentResult contentResult,
                Long visitCount) {
            return new ContentResult(
                    contentResult.getContentId(),
                    contentResult.getTitle(),
                    contentResult.getCategory(),
                    contentResult.getPictureUrl(),
                    visitCount);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ArtistResult {
        private Long artistId;
        private String artistName;
        private String artistPictureUrl;
        private Boolean isGroup;
        private Long relatedVerificationsCount;

        public static ArtistResult from(
                com.tracek.domain.artist.application.dto.ArtistResult artist) {
            return new ArtistResult(
                    artist.getId(),
                    artist.getName(),
                    artist.getPictureUrl(),
                    artist.getIsGroup(),
                    null);
        }

        public static ArtistResult of(
                com.tracek.domain.artist.application.dto.ArtistResult artist, Long visitCount) {
            return new ArtistResult(
                    artist.getId(),
                    artist.getName(),
                    artist.getPictureUrl(),
                    artist.getIsGroup(),
                    visitCount);
        }
    }
}
