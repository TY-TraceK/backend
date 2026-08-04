package com.tracek.domain.artist.domain.repository;

import com.tracek.domain.artist.domain.model.Artist;
import java.util.List;
import java.util.Optional;

public interface ArtistRepository {
    Optional<Artist> findById(Long id);

    List<Artist> findAll();

    Artist save(Artist artist);

    void deleteById(Long id);
}
