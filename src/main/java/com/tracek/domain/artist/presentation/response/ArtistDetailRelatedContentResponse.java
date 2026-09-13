package com.tracek.domain.artist.presentation.response;

import com.tracek.domain.artist.application.dto.ArtistDetailRelatedContentResult;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtistDetailRelatedContentResponse {
    private ArtistInfoResponse artistInfo;
    private List<ContentResponse> contents;

    public static ArtistDetailRelatedContentResponse from(ArtistDetailRelatedContentResult result) {
        List<ContentResponse> contentResponses =
                result.getContents().stream().map(ContentResponse::from).toList();
        return new ArtistDetailRelatedContentResponse(
                ArtistInfoResponse.from(result.getArtistInfo()), contentResponses);
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
                ArtistDetailRelatedContentResult.ArtistInfo artistInfo) {
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
    public static class ContentResponse {
        private Long contentId;
        private String contentTitle;
        private String contentCategory;
        private String contentPictureUrl;
        private Boolean isFixed; // 아티스트-콘텐츠 고정 출연 여부
        private List<RelatedLocationResponse> relatedLocations;

        public static ContentResponse from(
                ArtistDetailRelatedContentResult.ContentResult contentResult) {
            return new ContentResponse(
                    contentResult.getContentId(),
                    contentResult.getContentTitle(),
                    contentResult.getContentCategory(),
                    contentResult.getContentPictureUrl(),
                    contentResult.getIsFixed(),
                    contentResult.getRelatedLocations() == null
                            ? null
                            : contentResult.getRelatedLocations().stream()
                                    .map(RelatedLocationResponse::from)
                                    .toList());
        }
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class RelatedLocationResponse {
        private Long locationId;
        private String locationName;
        private Long relatedVisitCount; // 방문 인증: 장소 X 아티스트 X 콘텐츠
        private List<EpisodeResponse> episodeInfo;

        public static RelatedLocationResponse from(
                ArtistDetailRelatedContentResult.RelatedLocationResult locationResult) {
            return new RelatedLocationResponse(
                    locationResult.getLocationId(),
                    locationResult.getLocationName(),
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
        private String episodeInfo; // 회차
        private String episodeVisitDate;
        private String note;

        public static EpisodeResponse from(ArtistDetailRelatedContentResult.EpisodeResult episode) {
            return new EpisodeResponse(
                    episode.getEpisodeId(),
                    episode.getEpisodeInfo(),
                    episode.getEpisodeVisitDate(),
                    episode.getNote());
        }
    }
}
