package com.tracek.domain.artist.application.dto;

import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.fan.application.dto.result.ArtistFanViewResult;
import com.tracek.domain.location.domain.model.Address;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistDetailRelatedLocationResult {
    private ArtistInfo artistInfo;
    private List<LocationResult> locations;

    public static ArtistDetailRelatedLocationResult of(
            ArtistInfo artistInfo, List<LocationResult> locations) {
        return new ArtistDetailRelatedLocationResult(artistInfo, locations);
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
        private Boolean isFan;
        private Long totalVerificationCount;
        private List<ArtistSummaryResult>
                relatedArtists; // group -> relatedMember, member -> relatedGroup

        public static ArtistInfo of(
                Artist artist,
                List<ArtistSummaryResult> relatedArtists,
                ArtistFanViewResult fanView) {
            return new ArtistInfo(
                    artist.getId(),
                    artist.getName(),
                    artist.getAlias(),
                    artist.getPictureUrl() == null ? null : artist.getPictureUrl().getImageUrl(),
                    artist.getGroup() == null ? null : artist.getGroup().getId(),
                    Boolean.TRUE.equals(artist.getIsGroup()),
                    fanView.fanCount(),
                    fanView.isFan(),
                    artist.getTotalVerificationCount(),
                    relatedArtists);
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
        private Address locationAddress;
        private Long relatedVisitCount; // 장소 X 아티스트
        private List<EpisodeResult> episodeInfo;

        public static LocationResult of(
                com.tracek.domain.location.application.dto.LocationResult location,
                Long relatedVisitCount,
                List<EpisodeResult> episodeInfo) {
            return new LocationResult(
                    location.getLocationId(),
                    location.getName(),
                    location.getCategory(),
                    location.getMainImageUrl(),
                    location.getAddress(),
                    relatedVisitCount,
                    episodeInfo);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class EpisodeResult {
        private Long episodeId;
        private String episodeInfo;
        private String episodeVisitDate;
        private String contentTitle;

        public static EpisodeResult from(
                com.tracek.domain.content.application.dto.EpisodeResult episode) {
            return new EpisodeResult(
                    episode.getId(),
                    episode.getEpisodeInfo(),
                    episode.getVisitDate(),
                    episode.getContent().getTitle());
        }
    }
}
