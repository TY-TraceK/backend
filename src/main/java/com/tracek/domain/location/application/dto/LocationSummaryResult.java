package com.tracek.domain.location.application.dto;

import com.tracek.domain.location.domain.model.Address;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationSummaryResult {

    private Long id;
    private String name;
    private LocationCategory category;
    private Address address;
    private String mainImageUrl;
    private Long likeCount;

    public static LocationSummaryResult from(Location location) {
        return new LocationSummaryResult(
                location.getId(),
                location.getName(),
                location.getCategory(),
                location.getAddress(),
                location.getMainImageUrl().getImageUrl(),
                location.getLikeCount());
    }
}
