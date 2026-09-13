package com.tracek.domain.content.presentation.response;

import com.tracek.domain.content.application.dto.ContentDetailResult;
import com.tracek.domain.location.domain.model.Address;
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
        private Long totalVerificationCount;
        private List<FixedArtistResponse> fixedArtists;

        public static ContentInfoResponse from(ContentDetailResult.ContentInfo contentInfo) {
            return new ContentInfoResponse(
                    contentInfo.getId(),
                    contentInfo.getTitle(),
                    contentInfo.getCategory(),
                    contentInfo.getPictureUrl(),
                    contentInfo.getTotalVerificationCount(),
                    contentInfo.getFixedArtists() == null
                            ? null
                            : contentInfo.getFixedArtists().stream()
                                    .map(FixedArtistResponse::from)
                                    .toList());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FixedArtistResponse {
        private Long artistId;
        private String artistName;
        private String artistPictureUrl;

        public static FixedArtistResponse from(ContentDetailResult.FixedArtistResult artist) {
            return new FixedArtistResponse(
                    artist.getArtistId(), artist.getArtistName(), artist.getArtistPictureUrl());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ContentLocationResponse {
        private Long locationId;
        private String locationName;
        private String locationCategory;
        private String locationPictureUrl;
        private Address locationAddress;
        private Long relatedVisitCount; // 방문 인증: 장소 X 콘텐츠
        private List<EpisodeResponse> episodeInfo;

        public static ContentLocationResponse from(
                ContentDetailResult.LocationResult locationResult) {

            return new ContentLocationResponse(
                    locationResult.getLocationId(),
                    locationResult.getLocationName(),
                    locationResult.getLocationCategory(),
                    locationResult.getLocationPictureUrl(),
                    locationResult.getLocationAddress(),
                    locationResult.getRelatedVisitCount(),
                    locationResult.getEpisodeInfo() == null
                            ? null
                            : locationResult.getEpisodeInfo().stream()
                                    .map(EpisodeResponse::from)
                                    .toList());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class EpisodeResponse {
        private Long episodeId;
        private String episodeInfo; // 회차
        private String episodeVisitDate;
        private String note;

        public static EpisodeResponse from(ContentDetailResult.EpisodeResult episode) {
            return new EpisodeResponse(
                    episode.getEpisodeId(),
                    episode.getEpisodeInfo(),
                    episode.getEpisodeVisitDate(),
                    episode.getNote());
        }
    }
}
