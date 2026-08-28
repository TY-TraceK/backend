package com.tracek.domain.location.application.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LocationSearchResult {
    private List<LocationInfo> locations;
    private boolean hasNext;
    private Long lastId;

    @Getter
    @AllArgsConstructor // QueryDSL Projections.constructor가 리플렉션으로 호출하므로 public이어야 함
    public static class LocationInfo {
        private Long id;
        private String name;
        private String category;
        private String address;
        private String mainImageUrl;
    }

    public static LocationSearchResult of(List<LocationInfo> locations, int requestedSize) {
        boolean hasNext = false;
        Long lastId = null;

        int validSize = (requestedSize <= 0) ? 20 : requestedSize;

        // No-Offset Slice
        if (locations.size() > validSize) {
            hasNext = true;
            locations = locations.subList(0, validSize);
        }

        if (!locations.isEmpty()) {
            lastId = locations.getLast().getId();
        }

        return new LocationSearchResult(locations, hasNext, lastId);
    }
}
