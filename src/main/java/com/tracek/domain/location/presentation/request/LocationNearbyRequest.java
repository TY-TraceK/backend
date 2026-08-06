package com.tracek.domain.location.presentation.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationNearbyRequest {

    @Schema(description = "현재 위치 위도", example = "37.5665")
    @NotNull(message = "위도는 필수 입력값입니다.")
    @Min(value = -90, message = "위도는 -90 이상이어야 합니다.")
    @Max(value = 90, message = "위도는 90 이하이어야 합니다.")
    private Double lat;

    @Schema(description = "현재 위치 경도", example = "126.9780")
    @NotNull(message = "경도는 필수 입력값입니다.")
    @Min(value = -180, message = "경도는 -180 이상이어야 합니다.")
    @Max(value = 180, message = "경도는 180 이하이어야 합니다.")
    private Double lng;

    @Schema(description = "조회 반경(미터), 미입력 시 기본 1000m", example = "1000")
    private Double radiusMeter;

    public static LocationNearbyRequest of(Double lat, Double lng, Double radiusMeter) {
        return new LocationNearbyRequest(lat, lng, radiusMeter != null ? radiusMeter : 1000.0);
    }

    public Double getRadiusMeter() {
        return radiusMeter != null ? radiusMeter : 1000.0; // 기본 반경 1km (1000m)
    }
}
