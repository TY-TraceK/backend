package com.tracek.domain.content.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ContentArtistPair {
    // content - artist 쌍을 표현하기 위한 dto
    private Long contentId;
    private Long artistId;

    public static ContentArtistPair of(Long contentId, Long artistId) {
        return new ContentArtistPair(contentId, artistId);
    }
}
