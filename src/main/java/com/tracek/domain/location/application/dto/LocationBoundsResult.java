package com.tracek.domain.location.application.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LocationBoundsResult {
    private List<LocationSearchResult.LocationInfo> locations;

    public static LocationBoundsResult of(List<LocationSearchResult.LocationInfo> locations) {
        return new LocationBoundsResult(locations);
    }
}
