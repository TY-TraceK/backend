package com.tracek.domain.vote.domain.repository;

import com.tracek.domain.vote.domain.model.Vote;
import java.util.List;

public interface VoteRepository {

    boolean hasAlreadyVotedLocation(Long userId, Long locationId);

    void deleteAllInBatch();

    List<Vote> findAll();

    Vote save(Vote vote);
}
