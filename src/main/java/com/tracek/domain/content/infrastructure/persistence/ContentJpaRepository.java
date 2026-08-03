package com.tracek.domain.content.infrastructure.persistence;

import com.tracek.domain.content.domain.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentJpaRepository extends JpaRepository<Content, Long> {}
