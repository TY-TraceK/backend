package com.tracek.domain.vote.application.service;

import com.tracek.domain.vote.application.dto.condition.VoteHistoriesSearchCondition;
import com.tracek.domain.vote.application.dto.condition.VoteStatusSearchCondition;
import com.tracek.domain.vote.application.dto.result.VoteHistoriesResult;
import com.tracek.domain.vote.application.dto.result.VoteStatusSearchResult;
import org.springframework.data.domain.Pageable;

public interface VoteQueryService {

    VoteStatusSearchResult getMyVoteStatus(VoteStatusSearchCondition condition);

    VoteHistoriesResult getMyHistories(VoteHistoriesSearchCondition condition, Pageable pageable);
}
