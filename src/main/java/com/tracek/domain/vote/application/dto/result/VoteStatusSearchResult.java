package com.tracek.domain.vote.application.dto.result;

import com.tracek.domain.vote.domain.model.Vote;
import java.time.LocalDate;
import java.util.Objects;
import lombok.Builder;

@Builder
public record VoteStatusSearchResult(Boolean isVoted, Long voteId, LocalDate targetDate) {

    public static VoteStatusSearchResult from(Vote vote, LocalDate targetDate) {
        return VoteStatusSearchResult.builder()
                .isVoted(Objects.nonNull(vote))
                .voteId(vote != null ? vote.getId() : null)
                .targetDate(targetDate)
                .build();
    }
}
