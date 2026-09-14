package com.tracek.domain.content.application.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ContentSearchResult {
    private List<ContentInfo> contents;
    private boolean hasNext;
    private Long lastId;

    @Getter
    @AllArgsConstructor // QueryDSL Projections.constructor가 리플렉션으로 호출하므로 public이어야 함
    public static class ContentInfo {
        private Long id;
        private String title;
        private String category;
        private String pictureUrl;
    }

    public static ContentSearchResult of(List<ContentInfo> contents, int requestedSize) {
        boolean hasNext = false;
        Long lastId = null;

        int validSize = (requestedSize <= 0) ? 20 : requestedSize;

        // No-Offset Slice
        if (contents.size() > validSize) {
            hasNext = true;
            contents = contents.subList(0, validSize);
        }

        if (!contents.isEmpty()) {
            lastId = contents.getLast().getId();
        }

        return new ContentSearchResult(contents, hasNext, lastId);
    }
}
