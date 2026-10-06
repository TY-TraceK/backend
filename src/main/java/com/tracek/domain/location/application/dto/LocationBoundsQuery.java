package com.tracek.domain.location.application.dto;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.domain.location.domain.model.LocationCategory;
import com.tracek.global.exception.CustomException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class LocationBoundsQuery {

    // 사용자 위치 미제공 시 폴백 기준점 - 부산광역시청
    private static final double BUSAN_CITY_HALL_LAT = 35.1796;
    private static final double BUSAN_CITY_HALL_LNG = 129.0756;
    private static final double DEFAULT_HALF_SPAN = 0.15; // MAX_SPAN의 절반

    // 줌아웃 상한 - 위경도 차이가 이보다 크면 조회를 막고 화면 확대를 유도
    private static final double MAX_SPAN = 0.3;

    // 이 폭 이하(카카오맵 레벨 7, 약 0.097° 까지)는 공간 인덱스(R-Tree), 그보다 넓으면 기존 B-Tree 조회
    // 넓은 범위는 R-Tree가 후보를 너무 많이 읽어 오히려 느려짐 (레벨 8 실측: B-Tree 4.5ms vs R-Tree 7.8ms)
    private static final double SPATIAL_INDEX_MAX_SPAN = 0.15;

    private double swLat;
    private double swLng;
    private double neLat;
    private double neLng;
    private LocationCategory category;
    private boolean archivedOnly;

    public static LocationBoundsQuery busanDefault() {
        return busanDefault(null, false);
    }

    public static LocationBoundsQuery busanDefault(String category, boolean archivedOnly) {
        return new LocationBoundsQuery(
                BUSAN_CITY_HALL_LAT - DEFAULT_HALF_SPAN,
                BUSAN_CITY_HALL_LNG - DEFAULT_HALF_SPAN,
                BUSAN_CITY_HALL_LAT + DEFAULT_HALF_SPAN,
                BUSAN_CITY_HALL_LNG + DEFAULT_HALF_SPAN,
                LocationCategory.from(category),
                archivedOnly);
    }

    public static LocationBoundsQuery of(
            Double swLat,
            Double swLng,
            Double neLat,
            Double neLng,
            String category,
            boolean archivedOnly) {
        validate(swLat, swLng, neLat, neLng);
        return new LocationBoundsQuery(
                swLat, swLng, neLat, neLng, LocationCategory.from(category), archivedOnly);
    }

    public boolean usesSpatialIndex() {
        return neLat - swLat <= SPATIAL_INDEX_MAX_SPAN && neLng - swLng <= SPATIAL_INDEX_MAX_SPAN;
    }

    private static void validate(Double swLat, Double swLng, Double neLat, Double neLng) {
        if (swLat == null || swLng == null || neLat == null || neLng == null) {
            throw new CustomException(LocationErrorCode.INVALID_BOUNDS);
        }
        if (swLat < -90
                || swLat > 90
                || neLat < -90
                || neLat > 90
                || swLng < -180
                || swLng > 180
                || neLng < -180
                || neLng > 180) {
            throw new CustomException(LocationErrorCode.INVALID_GEO_LOCATION);
        }
        if (swLat >= neLat || swLng >= neLng) {
            throw new CustomException(LocationErrorCode.INVALID_BOUNDS);
        }
        if (neLat - swLat > MAX_SPAN || neLng - swLng > MAX_SPAN) {
            throw new CustomException(LocationErrorCode.BOUNDS_TOO_WIDE);
        }
    }
}
