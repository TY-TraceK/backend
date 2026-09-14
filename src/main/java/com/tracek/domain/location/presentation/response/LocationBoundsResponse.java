package com.tracek.domain.location.presentation.response;

import com.tracek.domain.location.application.dto.LocationBoundsResult;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationBoundsResponse {
    private List<LocationSearchResponse.LocationSearchElement> locations;

    public static LocationBoundsResponse from(LocationBoundsResult result) {
        List<LocationSearchResponse.LocationSearchElement> elements =
                result.getLocations().stream()
                        .map(LocationSearchResponse.LocationSearchElement::from)
                        .toList();

        return new LocationBoundsResponse(elements);
    }
}
