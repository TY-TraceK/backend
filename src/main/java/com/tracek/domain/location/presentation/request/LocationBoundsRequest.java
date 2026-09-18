package com.tracek.domain.location.presentation.request;

import com.tracek.domain.location.application.dto.LocationBoundsQuery;

public record LocationBoundsRequest(
        Double southwestLatitude,
        Double southwestLongitude,
        Double northeastLatitude,
        Double northeastLongitude,
        String category,
        boolean archivedOnly) {

    public LocationBoundsQuery toQuery() {
        if (southwestLatitude == null
                && southwestLongitude == null
                && northeastLatitude == null
                && northeastLongitude == null) {
            return LocationBoundsQuery.busanDefault(category, archivedOnly);
        }
        return LocationBoundsQuery.of(
                southwestLatitude,
                southwestLongitude,
                northeastLatitude,
                northeastLongitude,
                category,
                archivedOnly);
    }
}
