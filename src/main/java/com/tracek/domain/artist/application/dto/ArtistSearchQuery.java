package com.tracek.domain.artist.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ArtistSearchQuery {
    private String keyword;
    private Long lastArtistId;
    private int size;

    public static ArtistSearchQuery of(String keyword, Long lastArtistId, int size) {
        return new ArtistSearchQuery(keyword, lastArtistId, size);
    }
}
