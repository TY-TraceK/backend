package com.tracek.domain.vote.application.dto.result;

import com.tracek.domain.vote.domain.model.Vote;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record VoteHistoriesIndividualResult(
        Long locationId,
        Long contentId,
        Long artistId,
        LocalDateTime votedTimeAt,
        LocalDate votedDate,
        String voteStatus) {

    public static VoteHistoriesIndividualResult from(Vote vote) {
        return new VoteHistoriesIndividualResult(
                vote.getVoteTarget().getLocationId(),
                vote.getVoteTarget().getContentId(),
                vote.getVoteTarget().getArtistId(),
                vote.getVotedAt(),
                vote.getValidVotedAt(),
                vote.getVoteStatus().name());
    }
}
