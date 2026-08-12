package com.tracek.domain.vote.application.dto.result;

import com.tracek.domain.vote.domain.model.Vote;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VoteCreateResult(Long voteId, String voteStatus, LocalDateTime votedAt) {

    public static VoteCreateResult from(Vote vote) {
        return VoteCreateResult.builder().voteId(vote.getId()).votedAt(vote.getVotedAt()).build();
    }
}
