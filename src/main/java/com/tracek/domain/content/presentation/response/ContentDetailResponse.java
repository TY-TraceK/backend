package com.tracek.domain.content.presentation.response;

import com.tracek.domain.content.application.dto.ContentDetailResult;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentDetailResponse {

    private ContentInfoResponse contentInfo;
    private List<ContentLocationResponse> locations;

    public static ContentDetailResponse from(ContentDetailResult result) {
        List<ContentLocationResponse> locationResponses =
                result.getLocations().stream().map(ContentLocationResponse::from).toList();

        return new ContentDetailResponse(
                ContentInfoResponse.from(result.getContentInfo()), locationResponses);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ContentInfoResponse {
        private Long id;
        private String title;
        private String category;
        private String pictureUrl;

        public static ContentInfoResponse from(ContentDetailResult.ContentInfo contentInfo) {
            return new ContentInfoResponse(
                    contentInfo.getId(),
                    contentInfo.getTitle(),
                    contentInfo.getCategory(),
                    contentInfo.getPictureUrl());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ContentLocationResponse {
        private Long locationId;
        private String locationName;
        private String locationCategory;
        private String locationPictureUrl;
        private List<ContentArtistResponse> artists;

        public static ContentLocationResponse from(
                ContentDetailResult.LocationResult locationResult) {
            List<ContentArtistResponse> artistResponses =
                    locationResult.getArtists().stream().map(ContentArtistResponse::from).toList();

            return new ContentLocationResponse(
                    locationResult.getLocationId(),
                    locationResult.getLocationName(),
                    locationResult.getLocationCategory(),
                    locationResult.getLocationPictureUrl(),
                    artistResponses);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ContentArtistResponse {
        private Long artistId;
        private String artistName;
        private String artistPictureUrl;

        public static ContentArtistResponse from(ContentDetailResult.ArtistResult artistResult) {
            return new ContentArtistResponse(
                    artistResult.getArtistId(),
                    artistResult.getArtistName(),
                    artistResult.getArtistPictureUrl());
        }
    }
}
