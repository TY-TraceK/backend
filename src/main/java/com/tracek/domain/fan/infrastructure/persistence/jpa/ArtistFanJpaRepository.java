package com.tracek.domain.fan.infrastructure.persistence.jpa;

import com.tracek.domain.fan.domain.model.ArtistFan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistFanJpaRepository extends JpaRepository<ArtistFan, Long> {}
