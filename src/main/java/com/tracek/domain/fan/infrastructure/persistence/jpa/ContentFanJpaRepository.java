package com.tracek.domain.fan.infrastructure.persistence.jpa;

import com.tracek.domain.fan.domain.model.ContentFan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentFanJpaRepository extends JpaRepository<ContentFan, Long> {}
