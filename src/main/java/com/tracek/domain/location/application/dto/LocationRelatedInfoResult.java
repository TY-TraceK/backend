package com.tracek.domain.location.application.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LocationRelatedInfoResult {
    private Long locationId;
    private String locationName;
    private String city;
    private List<RelatedContentGroup> relatedContentGroups;

    public static LocationRelatedInfoResult of(
            Long locationId,
            String locationName,
            String city,
            List<RelatedContentGroup> relatedContentGroups) {
        return new LocationRelatedInfoResult(locationId, locationName, city, relatedContentGroups);
    }

    // 연관 콘텐츠-아티스트 세트 DTO (내부 클래스)
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RelatedContentGroup {
        private Long contentId;
        private String contentTitle;
        private String contentCategory; // ALBUM, MV, VARIETY 등
        private String contentPictureUrl;
        private List<RelatedArtistResult> relatedArtists;

        public static RelatedContentGroup of(
                Long contentId,
                String contentTitle,
                String contentCategory,
                String contentPictureUrl,
                List<RelatedArtistResult> relatedArtists) {
            return new RelatedContentGroup(
                    contentId, contentTitle, contentCategory, contentPictureUrl, relatedArtists);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RelatedArtistResult {
        private Long artistId;
        private String artistName;
        private String artistPictureUrl;
        private Boolean isGroup;

        public static RelatedArtistResult of(
                Long artistId, String artistName, String artistPictureUrl, Boolean isGroup) {
            return new RelatedArtistResult(artistId, artistName, artistPictureUrl, isGroup);
        }
    }
}
