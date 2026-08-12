package com.tracek.domain.vote.infrastructure.persistence;

import com.tracek.domain.vote.domain.model.Vote;
import com.tracek.domain.vote.domain.repository.VoteRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoteRepositoryImpl implements VoteRepository {

    private final VoteJpaRepository voteJpaRepository;

    @Override
    public boolean existsByVoteOwnerAndLocationId(Long userId, Long locationId) {
        return voteJpaRepository.existsByVoteOwnerAndVoteTarget_LocationId(userId, locationId);
    }

    @Override
    public void deleteAllInBatch() {
        voteJpaRepository.deleteAllInBatch();
    }

    @Override
    public List<Vote> findAll() {
        return voteJpaRepository.findAll();
    }

    @Override
    public Vote save(Vote vote) {
        return voteJpaRepository.save(vote);
    }
}
