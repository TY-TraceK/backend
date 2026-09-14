package com.tracek.domain.artist.application.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ArtistSearchResult {
    private List<ArtistInfo> artists;
    private boolean hasNext;
    private Long lastId;

    @Getter
    @AllArgsConstructor // QueryDSL Projections.constructor가 리플렉션으로 호출하므로 public이어야 함
    public static class ArtistInfo {
        private Long id;
        private String name;
        private String alias;
        private String pictureUrl;
        private Long groupId;
        private Boolean isGroup;
    }

    public static ArtistSearchResult of(List<ArtistInfo> artists, int requestedSize) {
        boolean hasNext = false;
        Long lastId = null;

        int validSize = (requestedSize <= 0) ? 20 : requestedSize;

        // No-Offset Slice
        // size + 1로 받아서 hasNext 판단 & slice
        if (artists.size() > validSize) {
            hasNext = true;
            artists = artists.subList(0, validSize);
        }

        if (!artists.isEmpty()) {
            lastId = artists.getLast().getId();
        }

        return new ArtistSearchResult(artists, hasNext, lastId);
    }
}
