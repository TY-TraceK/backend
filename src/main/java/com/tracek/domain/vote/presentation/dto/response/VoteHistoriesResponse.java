package com.tracek.domain.vote.presentation.dto.response;

import com.tracek.domain.vote.application.dto.result.VoteHistoriesIndividualResult;
import com.tracek.domain.vote.application.dto.result.VoteHistoriesResult;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record VoteHistoriesResponse(Page<VoteHistoriesIndividualResponse> histories) {

    public static VoteHistoriesResponse from(VoteHistoriesResult result) {

        return new VoteHistoriesResponse(
                result.histories().map(VoteHistoriesIndividualResponse::from));
    }
}

@Builder
record VoteHistoriesIndividualResponse(
        Long locationId,
        Long contentId,
        Long artistId,
        LocalDateTime votedTimeAt,
        LocalDate votedDate,
        String voteStatus) {

    public static VoteHistoriesIndividualResponse from(VoteHistoriesIndividualResult result) {
        return VoteHistoriesIndividualResponse.builder()
                .artistId(result.artistId())
                .contentId(result.contentId())
                .locationId(result.locationId())
                .votedDate(result.votedDate())
                .votedTimeAt(result.votedTimeAt())
                .voteStatus(result.voteStatus())
                .build();
    }
}
