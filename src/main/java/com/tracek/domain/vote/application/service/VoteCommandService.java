package com.tracek.domain.vote.application.service;

import com.tracek.domain.vote.application.dto.command.VoteCancelCommand;
import com.tracek.domain.vote.application.dto.command.VoteCreateCommand;
import com.tracek.domain.vote.application.dto.result.VoteCancelResult;
import com.tracek.domain.vote.application.dto.result.VoteCreateResult;

public interface VoteCommandService {

    VoteCreateResult createVote(VoteCreateCommand command);

    VoteCancelResult cancelVote(VoteCancelCommand command);
}
