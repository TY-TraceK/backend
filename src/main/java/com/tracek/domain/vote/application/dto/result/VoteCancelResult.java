package com.tracek.domain.vote.application.dto.result;

import com.tracek.domain.vote.domain.model.Vote;
import lombok.Builder;

@Builder
public record VoteCancelResult(Long voteId, String voteStatus) {

    public static VoteCancelResult from(Vote vote) {
        return VoteCancelResult.builder()
                .voteId(vote.getId())
                .voteStatus(vote.getVoteStatus().toString())
                .build();
    }
}
