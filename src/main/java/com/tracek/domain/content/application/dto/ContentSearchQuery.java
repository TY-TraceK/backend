package com.tracek.domain.content.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ContentSearchQuery {
    private String keyword;
    private Long lastContentId;
    private int size;

    public static ContentSearchQuery of(String keyword, Long lastContentId, int size) {
        return new ContentSearchQuery(keyword, lastContentId, size);
    }
}
