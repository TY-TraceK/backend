package com.tracek.domain.vote.application.service.impl;

import com.tracek.domain.vote.application.dto.condition.VoteStatusSearchCondition;
import com.tracek.domain.vote.application.dto.result.VoteStatusSearchResult;
import com.tracek.domain.vote.application.service.VoteQueryService;
import com.tracek.domain.vote.domain.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VoteQueryServiceImpl implements VoteQueryService {

    private final VoteRepository voteRepository;

    @Override
    public VoteStatusSearchResult getMyVoteStatus(VoteStatusSearchCondition condition) {
        return VoteStatusSearchResult.from(
                voteRepository
                        .findUserLocationVoteByDate(
                                condition.userId(), condition.locationId(), condition.date())
                        .orElse(null),
                condition.date());
    }
}
