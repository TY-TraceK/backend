package com.tracek.domain.vote.infrastructure.persistence;

import com.tracek.domain.vote.domain.enums.VoteStatus;
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
    public boolean hasAlreadyVotedLocation(Long userId, Long locationId) {
        return voteJpaRepository.existsByVoteOwnerAndVoteTarget_LocationIdAndVoteStatus(
                userId, locationId, VoteStatus.VALID);
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
