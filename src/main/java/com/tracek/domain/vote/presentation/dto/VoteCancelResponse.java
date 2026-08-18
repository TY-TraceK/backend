package com.tracek.domain.vote.presentation.dto;

import com.tracek.domain.vote.application.dto.result.VoteCancelResult;
import lombok.Builder;

@Builder
public record VoteCancelResponse(Long voteId, String voteStatus) {

    public static VoteCancelResponse from(VoteCancelResult result) {
        return VoteCancelResponse.builder()
                .voteId(result.voteId())
                .voteStatus(result.voteStatus())
                .build();
    }
}
