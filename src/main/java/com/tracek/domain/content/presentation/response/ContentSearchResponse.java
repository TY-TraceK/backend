package com.tracek.domain.content.presentation.response;

import com.tracek.domain.content.application.dto.ContentSearchResult;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentSearchResponse {
    private List<ContentSearchElement> contents;
    private boolean hasNext;
    private Long lastId;

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ContentSearchElement {
        private Long id;
        private String title;
        private String category;
        private String pictureUrl;

        public static ContentSearchElement from(ContentSearchResult.ContentInfo result) {
            return new ContentSearchElement(
                    result.getId(),
                    result.getTitle(),
                    result.getCategory(),
                    result.getPictureUrl());
        }
    }

    public static ContentSearchResponse from(ContentSearchResult result) {
        List<ContentSearchElement> elements =
                result.getContents().stream().map(ContentSearchElement::from).toList();

        return new ContentSearchResponse(elements, result.isHasNext(), result.getLastId());
    }
}
