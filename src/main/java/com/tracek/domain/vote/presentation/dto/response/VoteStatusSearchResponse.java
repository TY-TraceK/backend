package com.tracek.domain.vote.presentation.dto.response;

import com.tracek.domain.vote.application.dto.result.VoteStatusSearchResult;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record VoteStatusSearchResponse(Boolean isVoted, Long voteId, LocalDate targetDate) {

    public static VoteStatusSearchResponse from(VoteStatusSearchResult result) {
        return VoteStatusSearchResponse.builder()
                .isVoted(result.isVoted())
                .voteId(result.voteId())
                .targetDate(result.targetDate())
                .build();
    }
}
