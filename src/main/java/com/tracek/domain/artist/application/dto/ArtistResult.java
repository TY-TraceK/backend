package com.tracek.domain.artist.application.dto;

import com.tracek.domain.artist.domain.model.Artist;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ArtistResult {
    private Long id;
    private String name;
    private String alias;
    private String pictureUrl;
    private String description;
    private Long groupId;

    public static ArtistResult from(Artist artist) {
        return new ArtistResult(
                artist.getId(),
                artist.getName(),
                artist.getAlias(),
                artist.getPictureUrl().getImageUrl(),
                artist.getDescription(),
                artist.getGroup() == null ? null : artist.getGroup().getId());
    }
}
