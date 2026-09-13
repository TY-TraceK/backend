package com.tracek.domain.content.application.dto;

import com.tracek.domain.content.domain.model.Episode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class EpisodeResult {
    private Long id;
    private ContentResult content;
    private String sourceUrl;
    private String episodeInfo;
    private String visitDate;
    private String note;

    public static EpisodeResult from(Episode episode) {
        return new EpisodeResult(
                episode.getId(),
                ContentResult.from(episode.getContent()),
                episode.getSourceUrl(),
                episode.getEpisodeInfo(),
                episode.getVisitDate(),
                episode.getNote());
    }
}
