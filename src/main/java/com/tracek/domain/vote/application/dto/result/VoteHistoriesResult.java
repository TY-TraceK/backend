package com.tracek.domain.vote.application.dto.result;

import com.tracek.domain.vote.domain.model.Vote;
import lombok.Builder;
import org.springframework.data.domain.Page;

@Builder
public record VoteHistoriesResult(Page<VoteHistoriesIndividualResult> histories) {

    public static VoteHistoriesResult from(Page<Vote> votePage) {
        return new VoteHistoriesResult(votePage.map(VoteHistoriesIndividualResult::from));
    }
}
