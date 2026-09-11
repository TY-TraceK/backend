package com.tracek.domain.artist.application.dto;

import com.tracek.domain.artist.domain.model.Artist;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistDetailResult {
    private ArtistInfo artistInfo;
    private List<LocationResult> locations;
    private List<ContentResult> contents;

    public static ArtistDetailResult from(
            ArtistInfo artistInfo, List<LocationResult> locations, List<ContentResult> contents) {
        return new ArtistDetailResult(artistInfo, locations, contents);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ArtistInfo {
        private Long id;
        private String name;
        private String alias;
        private String pictureUrl;
        private Long groupId;
        private Boolean isGroup;
        private Long fanCount;
        private Long totalVerificationCount;

        public static ArtistInfo of(Artist artist) {
            return new ArtistInfo(
                    artist.getId(),
                    artist.getName(),
                    artist.getAlias(),
                    artist.getPictureUrl() == null ? null : artist.getPictureUrl().getImageUrl(),
                    artist.getGroup() == null ? null : artist.getGroup().getId(),
                    artist.getGroup() != null,
                    artist.getFanCount(),
                    artist.getTotalVerificationCount());
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

        public static LocationResult of(
                com.tracek.domain.location.application.dto.LocationResult location) {
            return new LocationResult(
                    location.getLocationId(),
                    location.getName(),
                    location.getCategory(),
                    location.getMainImageUrl());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ContentResult {
        private Long contentId;
        private String contentTitle;
        private String contentCategory;
        private String contentPictureUrl;

        public static ContentResult of(
                com.tracek.domain.content.application.dto.ContentResult contentResult) {
            return new ContentResult(
                    contentResult.getContentId(),
                    contentResult.getTitle(),
                    contentResult.getCategory(),
                    contentResult.getPictureUrl());
        }
    }
}
