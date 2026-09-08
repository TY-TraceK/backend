package com.tracek.domain.location.presentation.response;

import com.tracek.domain.location.application.dto.LocationRelatedInfoResult;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationRelatedInfoResponse {

    private Long locationId;
    private String locationName;
    private String city;
    private List<RelatedContentGroupResponse> relatedContentGroups;

    public static LocationRelatedInfoResponse from(LocationRelatedInfoResult result) {
        List<RelatedContentGroupResponse> groupResponses =
                result.getRelatedContentGroups().stream()
                        .map(RelatedContentGroupResponse::from)
                        .toList();

        return new LocationRelatedInfoResponse(
                result.getLocationId(), result.getLocationName(), result.getCity(), groupResponses);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RelatedContentGroupResponse {
        private Long contentId;
        private String contentTitle;
        private String contentCategory;
        private String contentPictureUrl;
        private List<RelatedArtistResponse> artists;

        public static RelatedContentGroupResponse from(
                LocationRelatedInfoResult.RelatedContentGroup result) {
            List<RelatedArtistResponse> artistResponses =
                    result.getRelatedArtists().stream().map(RelatedArtistResponse::from).toList();

            return new RelatedContentGroupResponse(
                    result.getContentId(),
                    result.getContentTitle(),
                    result.getContentCategory(),
                    result.getContentPictureUrl(),
                    artistResponses);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RelatedArtistResponse {
        private Long artistId;
        private String artistName;
        private String artistPictureUrl;
        private Boolean isGroup;

        public static RelatedArtistResponse from(
                LocationRelatedInfoResult.RelatedArtistResult result) {
            return new RelatedArtistResponse(
                    result.getArtistId(),
                    result.getArtistName(),
                    result.getArtistPictureUrl(),
                    result.getIsGroup());
        }
    }
}
