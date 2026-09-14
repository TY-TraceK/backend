package com.tracek.domain.artist.presentation.response;

import com.tracek.domain.artist.application.dto.ArtistSearchResult;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtistSearchResponse {
    private List<ArtistSearchElement> artists;
    private boolean hasNext;
    private Long lastId;

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ArtistSearchElement {
        private Long id;
        private String name;
        private String alias;
        private String pictureUrl;
        private Long groupId;
        private Boolean isGroup;

        public static ArtistSearchElement from(ArtistSearchResult.ArtistInfo result) {
            return new ArtistSearchElement(
                    result.getId(),
                    result.getName(),
                    result.getAlias(),
                    result.getPictureUrl(),
                    result.getGroupId(),
                    result.getIsGroup());
        }
    }

    public static ArtistSearchResponse from(ArtistSearchResult result) {
        List<ArtistSearchElement> elements =
                result.getArtists().stream().map(ArtistSearchElement::from).toList();

        return new ArtistSearchResponse(elements, result.isHasNext(), result.getLastId());
    }
}
