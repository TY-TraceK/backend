package com.tracek.domain.vote.application.dto.command;

import lombok.Builder;

@Builder
public record VoteCreateCommand(
        Long userId,
        Long locationId,
        Long locationContentArtistId,
        String voteTargetNameSnapShot) {}
