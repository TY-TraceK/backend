package com.tracek.domain.artist.presentation.response;

import com.tracek.domain.artist.application.dto.ArtistDetailResult;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtistDetailResponse {

    private ArtistInfoResponse artistInfo;
    private List<ArtistContentResponse> contents;

    public static ArtistDetailResponse from(ArtistDetailResult result) {
        List<ArtistContentResponse> contentResponses =
                result.getContents().stream().map(ArtistContentResponse::from).toList();

        return new ArtistDetailResponse(
                ArtistInfoResponse.from(result.getArtistInfo()), contentResponses);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ArtistInfoResponse {
        private Long id;
        private String name;
        private String alias;
        private String pictureUrl;
        private String description;
        private Long groupId;

        public static ArtistInfoResponse from(ArtistDetailResult.ArtistInfo artistInfo) {
            return new ArtistInfoResponse(
                    artistInfo.getId(),
                    artistInfo.getName(),
                    artistInfo.getAlias(),
                    artistInfo.getPictureUrl(),
                    artistInfo.getDescription(),
                    artistInfo.getGroupId());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ArtistContentResponse {
        private Long contentId;
        private String contentTitle;
        private String contentCategory;
        private String contentPictureUrl;
        private List<ArtistLocationResponse> locations;

        public static ArtistContentResponse from(ArtistDetailResult.ContentResult contentResult) {
            List<ArtistLocationResponse> locationResponses =
                    contentResult.getLocations().stream()
                            .map(ArtistLocationResponse::from)
                            .toList();

            return new ArtistContentResponse(
                    contentResult.getContentId(),
                    contentResult.getContentTitle(),
                    contentResult.getContentCategory(),
                    contentResult.getContentPictureUrl(),
                    locationResponses);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ArtistLocationResponse {
        private Long locationId;
        private String locationName;
        private String locationCategory;
        private String locationPictureUrl;

        public static ArtistLocationResponse from(
                ArtistDetailResult.LocationResult locationResult) {
            return new ArtistLocationResponse(
                    locationResult.getLocationId(),
                    locationResult.getLocationName(),
                    locationResult.getLocationCategory(),
                    locationResult.getLocationPictureUrl());
        }
    }
}
