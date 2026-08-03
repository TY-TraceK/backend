package com.tracek.domain.location.application.dto;

import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.location.domain.model.Address;
import com.tracek.domain.location.domain.model.GeoLocation;
import com.tracek.domain.location.domain.model.Location;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationResult {
    private Long locationId;
    private String name;
    private String category;
    private Long likeCount;
    private Address address;
    private GeoLocation geoLocation;
    private List<LocationImageResult> images;

    public static LocationResult of(Location location, List<LocationImageResult> imageResults) {
        return new LocationResult(
                location.getId(),
                location.getName(),
                location.getCategory(),
                location.getLikeCount(),
                location.getAddress(),
                location.getGeoLocation(),
                imageResults == null ? Collections.emptyList() : imageResults);
    }

    // 단건 기본 생성 (이미지 없이)
    public static LocationResult from(Location location) {
        return of(location, Collections.emptyList());
    }

    // 내부 이미지 응답용 Result DTO
    @Getter
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class LocationImageResult {
        private Long imageId;
        private String imageUrl;
        private Boolean isMain;
        private Integer displayOrder;

        public static LocationImageResult of(
                ImageResult imageResult, Boolean isMain, Integer displayOrder) {
            return new LocationImageResult(
                    imageResult.getId(), imageResult.getImageUrl(), isMain, displayOrder);
        }
    }
}
