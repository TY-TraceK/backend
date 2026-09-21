package com.tracek.domain.location.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TourImageResult {
    private String imageUrl;
    private String smallImageUrl;

    public static TourImageResult of(String imageUrl, String smallImageUrl) {
        return new TourImageResult(imageUrl, smallImageUrl);
    }
}
