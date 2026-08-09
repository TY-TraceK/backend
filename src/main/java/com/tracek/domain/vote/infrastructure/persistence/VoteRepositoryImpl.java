package com.tracek.domain.vote.infrastructure.persistence;

import com.tracek.domain.vote.domain.repository.VoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@RequiredArgsConstructor
@Repository
public class VoteRepositoryImpl implements VoteRepository {

  private final VoteJpaRepository voteJpaRepository;
}
