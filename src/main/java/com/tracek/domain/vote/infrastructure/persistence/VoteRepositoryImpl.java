package com.tracek.domain.vote.infrastructure.persistence;

import com.tracek.domain.vote.domain.enums.VoteStatus;
import com.tracek.domain.vote.domain.model.Vote;
import com.tracek.domain.vote.domain.model.VoteHistoryCriteria;
import com.tracek.domain.vote.domain.repository.VoteRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoteRepositoryImpl implements VoteRepository {

    private final VoteJpaRepository voteJpaRepository;
    private final VoteQueryDslRepository voteQueryDslRepository;

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

    @Override
    public Optional<Vote> findById(Long voteId) {
        return voteJpaRepository.findById(voteId);
    }

    @Override
    public Optional<Vote> findUserLocationVoteByDate(Long userId, Long locationId, LocalDate date) {
        return voteJpaRepository.findByVoteOwnerAndVoteTarget_LocationIdAndValidVotedAt(
                userId, locationId, date);
    }

    @Override
    public Page<Vote> findHistoriesByCriteria(
            VoteHistoryCriteria voteHistoryCriteria, Pageable pageable) {
        return voteQueryDslRepository.findHistoriesByCriteria(voteHistoryCriteria, pageable);
    }
}
