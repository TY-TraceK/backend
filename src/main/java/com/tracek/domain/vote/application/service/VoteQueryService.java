package com.tracek.domain.vote.application.service;

import com.tracek.domain.vote.application.dto.condition.VoteStatusSearchCondition;
import com.tracek.domain.vote.application.dto.result.VoteStatusSearchResult;

public interface VoteQueryService {

    VoteStatusSearchResult getMyVoteStatus(VoteStatusSearchCondition condition);
}
