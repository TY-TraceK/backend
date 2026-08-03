package com.tracek.domain.image.infrastructure.persistence;

import com.tracek.domain.image.domain.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageJpaRepository extends JpaRepository<Image, Long> {}
