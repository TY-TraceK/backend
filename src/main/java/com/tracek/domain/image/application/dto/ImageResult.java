package com.tracek.domain.image.application.dto;

import com.tracek.domain.image.domain.model.Image;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageResult {
    private Long id;
    private String imageUrl;

    public static ImageResult from(Image image) {
        return new ImageResult(image.getId(), image.getImageUrl().getImageUrl());
    }
}
