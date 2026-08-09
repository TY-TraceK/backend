package com.tracek.domain.content.application.dto;

import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.ContentCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentSummaryResult {

    private Long id;
    private String title;
    private ContentCategory category;
    private String pictureUrl;

    public static ContentSummaryResult from(Content content) {
        return new ContentSummaryResult(
                content.getId(),
                content.getTitle(),
                content.getCategory(),
                content.getPictureUrl().getImageUrl());
    }
}
