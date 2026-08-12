package com.tracek.domain.vote.presentation.dto.request;

import com.tracek.domain.vote.application.dto.command.VoteCreateCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record VoteCreateRequest(
        @NotNull Long locationId,
        @NotNull Long locationContentArtistId,
        @NotBlank String voteTargetNameSnapShot) {

    public VoteCreateCommand toCommand(Long userId) {
        return VoteCreateCommand.builder()
                .userId(userId)
                .locationContentArtistId(locationContentArtistId)
                .locationId(locationId)
                .voteTargetNameSnapShot(voteTargetNameSnapShot)
                .build();
    }
}
