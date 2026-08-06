package com.tracek.domain.artist.infrasructure.persistence;

import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.artist.domain.repository.ArtistRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArtistRepositoryImpl implements ArtistRepository {
    private final ArtistJpaRepository artistJpaRepository;

    @Override
    public Optional<Artist> findById(Long id) {
        return artistJpaRepository.findById(id);
    }

    @Override
    public List<Artist> findAllByIds(List<Long> ids) {
        return artistJpaRepository.findAllById(ids);
    }

    @Override
    public List<Artist> findAll() {
        return artistJpaRepository.findAll();
    }

    @Override
    public Artist save(Artist artist) {
        return artistJpaRepository.save(artist);
    }

    @Override
    public void deleteById(Long id) {
        artistJpaRepository.deleteById(id);
    }
}
