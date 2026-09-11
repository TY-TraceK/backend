package com.tracek.domain.visitVerification.presentation.dto.request;

import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCreateCommand;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record VisitVerificationCreateRequest(
        @NotNull Long locationId,
        @NotNull Long contentId,
        Long artistId,
        @NotNull
                @DecimalMin(value = "-90.0", message = "위도는 -90 이상이어야 합니다.")
                @DecimalMax(value = "90.0", message = "위도는 90 이하여야 합니다.")
                Double latitude,
        @NotNull
                @DecimalMin(value = "-180.0", message = "경도는 -180 이상이어야 합니다.")
                @DecimalMax(value = "180.0", message = "경도는 180 이하여야 합니다.")
                Double longitude) {

    public VisitVerificationCreateCommand toCommand(Long userId) {
        return VisitVerificationCreateCommand.builder()
                .userId(userId)
                .locationId(locationId)
                .contentId(contentId)
                .artistId(artistId)
                .latitude(latitude)
                .longitude(longitude)
                .build();
    }
}
