package com.tracek.domain.location.application.dto;

import com.tracek.domain.location.domain.model.Location;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationSimpleResult {

    private Long id;
    private String name;
    private String mainImageUrl;

    public static LocationSimpleResult from(Location location) {
        return new LocationSimpleResult(
                location.getId(),
                location.getName(),
                location.getMainImageUrl() == null
                        ? null
                        : location.getMainImageUrl().getImageUrl());
    }
}
