package com.tracek.domain.vote.infrastructure.persistence;

import com.tracek.domain.vote.domain.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteJpaRepository extends JpaRepository<Vote, Long> {}
