package com.tracek.domain.content.presentation.response;

import com.tracek.domain.content.application.dto.ContentSummaryResult;
import com.tracek.domain.content.domain.model.ContentCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentSummaryResponse {

    private Long id;
    private String title;
    private ContentCategory category;
    private String pictureUrl;

    public static ContentSummaryResponse from(ContentSummaryResult content) {
        return new ContentSummaryResponse(
                content.getId(),
                content.getTitle(),
                content.getCategory(),
                content.getPictureUrl());
    }
}
