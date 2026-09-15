package com.tracek.domain.ranking.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record LocationRankingView(
        Integer rank,
        Long locationId,
        String cityName,
        String locationName,
        LocalDateTime lastUpdateAt,
        long totalVerificationCount) {}
