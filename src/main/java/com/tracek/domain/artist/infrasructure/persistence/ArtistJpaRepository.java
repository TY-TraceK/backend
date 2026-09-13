package com.tracek.domain.artist.infrasructure.persistence;

import com.tracek.domain.artist.domain.model.Artist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtistJpaRepository extends JpaRepository<Artist, Long> {
    List<Artist> findByGroupId(Long groupId);
}
