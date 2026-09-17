package com.tracek.domain.fan.infrastructure.persistence.jpa;

import com.tracek.domain.fan.domain.model.ArtistFan;
import com.tracek.domain.fan.domain.model.ArtistFanId;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistFanJpaRepository extends JpaRepository<ArtistFan, ArtistFanId> {

    long countByUserIdAndArtistId(Long userId, Long artistId);

    List<ArtistFan> findAllByUserId(Long userId);

    Integer countArtistFanByArtistId(Long artistId);

    Integer countArtistFanByUserId(Long userId);
}
