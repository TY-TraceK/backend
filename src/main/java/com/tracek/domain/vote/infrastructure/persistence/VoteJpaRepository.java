package com.tracek.domain.vote.infrastructure.persistence;

import com.tracek.domain.vote.domain.enums.VoteStatus;
import com.tracek.domain.vote.domain.model.Vote;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteJpaRepository extends JpaRepository<Vote, Long> {

    boolean existsByVoteOwnerAndVoteTarget_LocationIdAndVoteStatus(
            Long voteOwner, Long locationId, VoteStatus voteStatus);

    Optional<Vote> findByVoteOwnerAndVoteTarget_LocationIdAndValidVotedAt(
            Long voteOwner, Long voteTarget_locationId, LocalDate validVotedAt);
}
