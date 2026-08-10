package com.tracek.domain.artist.application.dto;

import com.tracek.domain.artist.domain.model.Artist;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtistSummaryResult {

    private Long id;
    private String name;
    private String alias;
    private String pictureUrl;
    private Long groupId;

    public static ArtistSummaryResult from(Artist artist) {
        return new ArtistSummaryResult(
                artist.getId(),
                artist.getName(),
                artist.getAlias(),
                artist.getPictureUrl().getImageUrl(),
                artist.getGroup() == null ? null : artist.getGroup().getId());
    }
}
