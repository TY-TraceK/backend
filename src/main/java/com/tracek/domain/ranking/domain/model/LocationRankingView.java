package com.tracek.domain.ranking.domain.model;

import java.time.LocalDateTime;

public record LocationRankingView(
        Integer rank,
        Long locationId,
        String cityName,
        String locationAddress,
        LocalDateTime lastUpdateAt,
        long totalVerificationCount) {}
