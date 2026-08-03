package com.tracek.domain.location.presentation.response;

import com.tracek.domain.location.application.dto.LocationNearbyResult;
import com.tracek.domain.location.domain.model.GeoLocation;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationNearbyResponse {

    private Long locationId;
    private String name;
    private String category;
    private GeoLocation geoLocation;
    private double distanceMeter;

    public static LocationNearbyResponse from(LocationNearbyResult result) {
        return new LocationNearbyResponse(
                result.getLocationId(),
                result.getName(),
                result.getCategory(),
                result.getGeoLocation(),
                result.getDistanceMeter());
    }
}
