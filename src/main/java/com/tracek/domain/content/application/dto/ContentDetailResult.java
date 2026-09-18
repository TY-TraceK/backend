package com.tracek.domain.content.application.dto;

import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.fan.application.dto.result.ContentFanViewResult;
import com.tracek.domain.location.domain.model.Address;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentDetailResult {
    private ContentInfo contentInfo;
    private List<LocationResult> locations;

    public static ContentDetailResult of(ContentInfo contentInfo, List<LocationResult> locations) {
        return new ContentDetailResult(contentInfo, locations);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ContentInfo {
        private Long id;
        private String title;
        private String category;
        private String pictureUrl;
        private Long totalVerificationCount;
        private Long fanCount;
        private Boolean isFan;
        private List<FixedArtistResult> fixedArtists;

        public static ContentInfo of(
                Content content,
                List<FixedArtistResult> fixedArtists,
                ContentFanViewResult fanView) {
            return new ContentInfo(
                    content.getId(),
                    content.getTitle(),
                    content.getCategory() == null ? null : content.getCategory().name(),
                    content.getPictureUrl().getImageUrl(),
                    content.getTotalVerificationCount(),
                    fanView.fanCount(),
                    fanView.isFan(),
                    fixedArtists);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FixedArtistResult {
        private Long artistId;
        private String artistName;
        private String artistPictureUrl;

        public static FixedArtistResult from(
                com.tracek.domain.artist.application.dto.ArtistResult artist) {
            return new FixedArtistResult(artist.getId(), artist.getName(), artist.getPictureUrl());
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
        private Long relatedVisitCount; // 방문 인증: 장소 X 콘텐츠
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
        private String episodeInfo; // 회차
        private String episodeVisitDate;
        private String note;

        public static EpisodeResult from(
                com.tracek.domain.content.application.dto.EpisodeResult episode) {
            return new EpisodeResult(
                    episode.getId(),
                    episode.getEpisodeInfo(),
                    episode.getVisitDate(),
                    episode.getNote());
        }
    }
}
