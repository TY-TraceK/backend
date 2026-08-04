package com.tracek.domain.content.application.dto;

import com.tracek.domain.content.domain.model.Content;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ContentResult {
    private Long id;
    private String title;
    private String category;
    private String pictureUrl;

    public static ContentResult from(Content content) {
        return new ContentResult(
                content.getId(),
                content.getTitle(),
                content.getCategory(),
                content.getPictureUrl().getImageUrl());
    }
}
