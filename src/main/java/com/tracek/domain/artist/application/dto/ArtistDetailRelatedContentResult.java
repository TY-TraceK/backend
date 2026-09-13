package com.tracek.domain.artist.application.dto;

import com.tracek.domain.artist.domain.model.Artist;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistDetailRelatedContentResult {
    private ArtistInfo artistInfo;
    private List<ContentResult> contents;

    public static ArtistDetailRelatedContentResult of(
            ArtistInfo artistInfo, List<ContentResult> contents) {
        return new ArtistDetailRelatedContentResult(artistInfo, contents);
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
        private Long totalVerificationCount;
        private List<ArtistSummaryResult>
                relatedArtists; // group -> relatedMember, member -> relatedGroup

        public static ArtistInfo of(Artist artist, List<ArtistSummaryResult> relatedArtists) {
            return new ArtistInfo(
                    artist.getId(),
                    artist.getName(),
                    artist.getAlias(),
                    artist.getPictureUrl() == null ? null : artist.getPictureUrl().getImageUrl(),
                    artist.getGroup() == null ? null : artist.getGroup().getId(),
                    Boolean.TRUE.equals(artist.getIsGroup()),
                    artist.getFanCount(),
                    artist.getTotalVerificationCount(),
                    relatedArtists);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ContentResult {
        private Long contentId;
        private String contentTitle;
        private String contentCategory;
        private String contentPictureUrl;
        private Boolean isFixed; // 아티스트-콘텐츠 고정 출연 여부
        private List<RelatedLocationResult> relatedLocations;

        public static ContentResult of(
                com.tracek.domain.content.application.dto.ContentResult contentResult,
                Boolean isFixed,
                List<RelatedLocationResult> relatedLocations) {
            return new ContentResult(
                    contentResult.getContentId(),
                    contentResult.getTitle(),
                    contentResult.getCategory(),
                    contentResult.getPictureUrl(),
                    isFixed,
                    relatedLocations);
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class RelatedLocationResult {
        private Long locationId;
        private String locationName;
        private Long relatedVisitCount; // 방문 인증: 장소 X 아티스트 X 콘텐츠
        private List<EpisodeResult> episodeInfo;

        public static RelatedLocationResult of(
                Long locationId,
                String locationName,
                Long relatedVisitCount,
                List<EpisodeResult> episodeInfo) {
            return new RelatedLocationResult(
                    locationId, locationName, relatedVisitCount, episodeInfo);
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
