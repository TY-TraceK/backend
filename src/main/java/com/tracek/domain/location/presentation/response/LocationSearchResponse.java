package com.tracek.domain.location.presentation.response;

import com.tracek.domain.location.application.dto.LocationSearchResult;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationSearchResponse {
    private List<LocationSearchElement> locations;
    private boolean hasNext;
    private Long lastId;

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationSearchElement {
        private Long id;
        private String name;
        private String category;
        private String address;
        private String mainImageUrl;

        public static LocationSearchElement from(LocationSearchResult.LocationInfo result) {
            return new LocationSearchElement(
                    result.getId(),
                    result.getName(),
                    result.getCategory(),
                    result.getAddress(),
                    result.getMainImageUrl());
        }
    }

    public static LocationSearchResponse from(LocationSearchResult result) {
        List<LocationSearchElement> elements =
                result.getLocations().stream().map(LocationSearchElement::from).toList();

        return new LocationSearchResponse(elements, result.isHasNext(), result.getLastId());
    }
}
