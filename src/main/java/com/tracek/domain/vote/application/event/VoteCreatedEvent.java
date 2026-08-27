package com.tracek.domain.vote.application.event;

import com.tracek.domain.vote.domain.model.Vote;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record VoteCreatedEvent(
        Long voteId,
        Long voteOwner,
        LocalDateTime votedAt,
        Long locationId,
        Long artistId,
        Long contentId) {

    public static VoteCreatedEvent from(Vote vote) {
        return VoteCreatedEvent.builder()
                .voteId(vote.getId())
                .voteOwner(vote.getVoteOwner())
                .votedAt(vote.getVotedAt())
                .locationId(vote.getVoteTarget().getLocationId())
                .artistId(vote.getVoteTarget().getArtistId())
                .contentId(vote.getVoteTarget().getContentId())
                .build();
    }
}
