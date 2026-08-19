package com.tracek.domain.vote.application.dto.condition;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record VoteStatusSearchCondition(Long userId, Long locationId, LocalDate date) {}
