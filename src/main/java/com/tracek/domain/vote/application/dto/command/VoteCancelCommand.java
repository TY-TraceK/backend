package com.tracek.domain.vote.application.dto.command;

import lombok.Builder;

@Builder
public record VoteCancelCommand(Long voteId, Long userId) {

    public static VoteCancelCommand of(Long voteId, Long userId) {
        return VoteCancelCommand.builder().voteId(voteId).userId(userId).build();
    }
}
