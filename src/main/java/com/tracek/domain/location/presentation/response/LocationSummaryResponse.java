package com.tracek.domain.location.presentation.response;

import com.tracek.domain.location.application.dto.LocationSummaryResult;
import com.tracek.domain.location.domain.model.Address;
import com.tracek.domain.location.domain.model.LocationCategory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationSummaryResponse {

    private Long id;
    private String name;
    private LocationCategory category;
    private Address address;
    private String mainImageUrl;
    private Long likeCount;

    public static LocationSummaryResponse from(LocationSummaryResult location) {
        return new LocationSummaryResponse(
                location.getId(),
                location.getName(),
                location.getCategory(),
                location.getAddress(),
                location.getMainImageUrl(),
                location.getLikeCount());
    }
}
