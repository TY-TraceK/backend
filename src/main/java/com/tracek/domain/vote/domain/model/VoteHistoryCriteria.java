package com.tracek.domain.vote.domain.model;

import com.tracek.domain.vote.domain.enums.VoteStatus;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VoteHistoryCriteria(
        Long userId,
        Long artistId,
        Long contentId,
        Long locationId,
        VoteStatus voteStatus,
        LocalDateTime startDate,
        LocalDateTime endDate) {}
