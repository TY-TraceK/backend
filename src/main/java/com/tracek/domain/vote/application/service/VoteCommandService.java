package com.tracek.domain.vote.application.service;

import com.tracek.domain.vote.application.dto.command.VoteCreateCommand;
import com.tracek.domain.vote.application.dto.result.VoteCreateResult;

public interface VoteCommandService {

    VoteCreateResult createVote(VoteCreateCommand command);
}
