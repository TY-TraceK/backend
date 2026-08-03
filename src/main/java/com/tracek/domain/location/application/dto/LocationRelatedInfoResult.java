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
    private List<RelatedItemResult> relatedItems;

    public static LocationRelatedInfoResult of(
            Long locationId, String locationName, List<RelatedItemResult> relatedItems) {
        return new LocationRelatedInfoResult(locationId, locationName, relatedItems);
    }

    // 연관 콘텐츠-아티스트 세트 DTO (내부 클래스)
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RelatedItemResult {
        private Long contentId;
        private String contentTitle;
        private String contentType; // ALBUM, MV, VARIETY 등
        private String contentPictureUrl;

        private Long artistId;
        private String artistName;
        private String artistPictureUrl;

        public static LocationRelatedInfoResult of(
                Long locationId, String locationName, List<RelatedItemResult> relatedItems) {
            return new LocationRelatedInfoResult(locationId, locationName, relatedItems);
        }

        public static RelatedItemResult of(
                Long contentId,
                String contentTitle,
                String contentType,
                String contentPictureUrl,
                Long artistId,
                String artistName,
                String artistPictureUrl) {
            return new RelatedItemResult(
                    contentId,
                    contentTitle,
                    contentType,
                    contentPictureUrl,
                    artistId,
                    artistName,
                    artistPictureUrl);
        }
    }
}
