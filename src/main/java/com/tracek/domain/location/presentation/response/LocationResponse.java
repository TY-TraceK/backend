package com.tracek.domain.location.presentation.response;

import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.domain.model.Address;
import com.tracek.domain.location.domain.model.GeoLocation;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationResponse {

    private Long locationId;
    private String name;
    private String category;
    private Long likeCount;
    private Address address;
    private GeoLocation geoLocation;
    private List<LocationImageResponse> images;

    public static LocationResponse from(LocationResult result) {
        List<LocationImageResponse> imageResponses =
                result.getImages().stream()
                        .map(LocationImageResponse::from)
                        .collect(Collectors.toList());

        return new LocationResponse(
                result.getLocationId(),
                result.getName(),
                result.getCategory(),
                result.getLikeCount(),
                result.getAddress(),
                result.getGeoLocation(),
                imageResponses);
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationImageResponse {
        private Long imageId;
        private String imageUrl;
        private Boolean isMain;
        private Integer displayOrder;

        public static LocationImageResponse from(LocationResult.LocationImageResult imageResult) {
            return new LocationImageResponse(
                    imageResult.getImageId(),
                    imageResult.getImageUrl(),
                    imageResult.getIsMain(),
                    imageResult.getDisplayOrder());
        }
    }
}
