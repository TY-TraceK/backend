package com.tracek.domain.location.application.dto;

import com.tracek.domain.location.domain.model.GeoLocation;
import com.tracek.domain.location.domain.model.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LocationNearbyResult {
    private Long locationId;
    private String name;
    private String category;
    private GeoLocation geoLocation;
    private double distanceMeter;

    public static LocationNearbyResult of(Location location, double distanceMeter) {
        return new LocationNearbyResult(
                location.getId(),
                location.getName(),
                location.getCategory(),
                location.getGeoLocation(),
                distanceMeter);
    }
}
