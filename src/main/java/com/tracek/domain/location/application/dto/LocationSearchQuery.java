package com.tracek.domain.location.application.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LocationSearchQuery {
    private String keyword;
    private String category;
    private Long lastLocationId;
    private int size;

    public static LocationSearchQuery of(
            String keyword, String category, Long lastLocationId, int size) {
        return new LocationSearchQuery(keyword, category, lastLocationId, size);
    }
}
