package com.tracek.domain.location.presentation.response;

import com.tracek.domain.location.application.dto.LocationSimpleResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationSimpleResponse {

    private Long id;
    private String name;
    private String mainImageUrl;

    public static LocationSimpleResponse from(LocationSimpleResult location) {
        return new LocationSimpleResponse(
                location.getId(), location.getName(), location.getMainImageUrl());
    }
}
