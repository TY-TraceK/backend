package com.tracek.domain.artist.presentation.response;

import com.tracek.domain.artist.application.dto.ArtistDetailRelatedLocationResult;
import com.tracek.domain.location.domain.model.Address;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtistDetailRelatedLocationResponse {
    private ArtistInfoResponse artistInfo;
    private List<LocationResponse> locations;

    public static ArtistDetailRelatedLocationResponse from(
            ArtistDetailRelatedLocationResult result) {
        List<LocationResponse> locationResponses =
                result.getLocations().stream().map(LocationResponse::from).toList();
        return new ArtistDetailRelatedLocationResponse(
                ArtistDetailRelatedLocationResponse.ArtistInfoResponse.from(result.getArtistInfo()),
                locationResponses);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ArtistInfoResponse {
        private Long id;
        private String name;
        private String alias;
        private String pictureUrl;
        private Long groupId;
        private Boolean isGroup;
        private Long fanCount;
        private Long totalVerificationCount;
        private List<ArtistSummaryResponse> relatedArtists;

        public static ArtistInfoResponse from(
                ArtistDetailRelatedLocationResult.ArtistInfo artistInfo) {
            return new ArtistInfoResponse(
                    artistInfo.getId(),
                    artistInfo.getName(),
                    artistInfo.getAlias(),
                    artistInfo.getPictureUrl(),
                    artistInfo.getGroupId(),
                    artistInfo.getIsGroup(),
                    artistInfo.getFanCount(),
                    artistInfo.getTotalVerificationCount(),
                    artistInfo.getRelatedArtists() == null
                            ? null
                            : artistInfo.getRelatedArtists().stream()
                                    .map(ArtistSummaryResponse::from)
                                    .toList());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationResponse {
        private Long locationId;
        private String locationName;
        private String locationCategory;
        private String locationPictureUrl;
        private Address locationAddress;
        private Long relatedVisitCount; // 장소 X 아티스트
        private List<EpisodeResponse> episodeInfo;

        public static LocationResponse from(
                ArtistDetailRelatedLocationResult.LocationResult locationResult) {
            return new LocationResponse(
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
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class EpisodeResponse {
        private Long episodeId;
        private String episodeInfo;
        private String episodeVisitDate;
        private String contentTitle;

        public static EpisodeResponse from(
                ArtistDetailRelatedLocationResult.EpisodeResult episode) {
            return new EpisodeResponse(
                    episode.getEpisodeId(),
                    episode.getEpisodeInfo(),
                    episode.getEpisodeVisitDate(),
                    episode.getContentTitle());
        }
    }
}
