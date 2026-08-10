package com.tracek.domain.location.presentation.response;

import com.tracek.domain.location.application.dto.LocationRelatedInfoResult;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationRelatedInfoResponse {

    private Long locationId;
    private String locationName;
    private List<RelatedItemResponse> relatedItems;

    public static LocationRelatedInfoResponse from(LocationRelatedInfoResult result) {
        List<RelatedItemResponse> itemResponses =
                result.getRelatedItems().stream()
                        .map(RelatedItemResponse::from)
                        .collect(Collectors.toList());

        return new LocationRelatedInfoResponse(
                result.getLocationId(), result.getLocationName(), itemResponses);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RelatedItemResponse {
        private Long contentArtistLocationId;

        private Long contentId;
        private String contentTitle;
        private String contentType;
        private String contentPictureUrl;

        private Long artistId;
        private String artistName;
        private String artistPictureUrl;

        public static RelatedItemResponse from(LocationRelatedInfoResult.RelatedItemResult result) {
            return new RelatedItemResponse(
                    result.getContentArtistLocationId(),
                    result.getContentId(),
                    result.getContentTitle(),
                    result.getContentType(),
                    result.getContentPictureUrl(),
                    result.getArtistId(),
                    result.getArtistName(),
                    result.getArtistPictureUrl());
        }
    }
}
