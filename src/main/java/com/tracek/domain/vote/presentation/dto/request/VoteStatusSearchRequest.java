package com.tracek.domain.vote.presentation.dto.request;

import com.tracek.domain.vote.application.dto.condition.VoteStatusSearchCondition;
import java.time.LocalDate;

public record VoteStatusSearchRequest(LocalDate date) {

    public VoteStatusSearchCondition toCondition(Long locationId, Long userId) {
        return VoteStatusSearchCondition.builder()
                .userId(userId)
                .locationId(locationId)
                .date(date == null ? LocalDate.now() : date)
                .build();
    }
}
