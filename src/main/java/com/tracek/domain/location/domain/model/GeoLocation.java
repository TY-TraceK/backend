package com.tracek.domain.location.domain.model;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.global.exception.CustomException;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GeoLocation {
    private double latitude;
    private double longitude;

    public static GeoLocation of(double latitude, double longitude) {
        validateRange(latitude, longitude);
        return new GeoLocation(latitude, longitude);
    }

    public static void validateRange(double latitude, double longitude) {
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            throw new CustomException(LocationErrorCode.INVALID_GEO_LOCATION);
        }
    }

    // 두 좌표 간 거리 계산 도메인 로직 (Haversine 공식)
    public double calculateDistanceMeterTo(GeoLocation target) {
        double earthRadius = 6371000; // 미터 단위
        double dLat = Math.toRadians(target.latitude - this.latitude);
        double dLng = Math.toRadians(target.longitude - this.longitude);
        double a =
                Math.sin(dLat / 2) * Math.sin(dLat / 2)
                        + Math.cos(Math.toRadians(this.latitude))
                                * Math.cos(Math.toRadians(target.latitude))
                                * Math.sin(dLng / 2)
                                * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}
