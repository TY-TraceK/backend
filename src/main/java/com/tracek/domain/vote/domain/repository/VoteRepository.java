package com.tracek.domain.vote.domain.repository;

import com.tracek.domain.vote.domain.model.Vote;
import java.util.List;

public interface VoteRepository {

    boolean existsByVoteOwnerAndLocationId(Long aLong, Long aLong1);

    void deleteAllInBatch();

    List<Vote> findAll();

    Vote save(Vote vote);
}
