package com.tracek.domain.vote.presentation.dto.response;

import com.tracek.domain.vote.application.dto.result.VoteCreateResult;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VoteCreateResponse(Long voteId, String voteStatus, LocalDateTime votedAt) {

    public static VoteCreateResponse from(VoteCreateResult result) {
        return VoteCreateResponse.builder()
                .voteId(result.voteId())
                .votedAt(result.votedAt())
                .voteStatus(result.voteStatus())
                .build();
    }
}
