package com.tracek.domain.vote.domain.repository;

import com.tracek.domain.vote.domain.model.Vote;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface VoteRepository {

    boolean hasAlreadyVotedLocation(Long userId, Long locationId);

    void deleteAllInBatch();

    List<Vote> findAll();

    Vote save(Vote vote);

    Optional<Vote> findById(Long voteId);

    Optional<Vote> findUserLocationVoteByDate(Long userId, Long locationID, LocalDate date);
}
