package com.tracek.domain.artist.domain.repository;

import com.tracek.domain.artist.domain.model.Artist;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ArtistRepository {
    Optional<Artist> findById(Long id);

    List<Artist> findAllByIds(List<Long> ids);

    List<Artist> findAll();

    Page<Artist> findAll(Pageable pageable);

    Artist save(Artist artist);

    void deleteById(Long id);
}
