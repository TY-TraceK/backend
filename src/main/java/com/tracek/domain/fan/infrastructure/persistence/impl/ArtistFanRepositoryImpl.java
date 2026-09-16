package com.tracek.domain.fan.infrastructure.persistence.impl;

import com.tracek.domain.fan.domain.model.ArtistFan;
import com.tracek.domain.fan.domain.model.ArtistFanId;
import com.tracek.domain.fan.domain.repository.ArtistFanRepository;
import com.tracek.domain.fan.infrastructure.persistence.jpa.ArtistFanJpaRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArtistFanRepositoryImpl implements ArtistFanRepository {

    private final ArtistFanJpaRepository artistFanJpaRepository;

    @Override
    public boolean existsById(ArtistFanId fanId) {
        return artistFanJpaRepository.existsById(fanId);
    }

    @Override
    public void saveAndFlush(ArtistFan artistFan) {
        artistFanJpaRepository.saveAndFlush(artistFan);
    }

    @Override
    public Optional<ArtistFan> findById(ArtistFanId fanId) {
        return artistFanJpaRepository.findById(fanId);
    }

    @Override
    public void delete(ArtistFanId fanId) {
        artistFanJpaRepository.deleteById(fanId);
    }
}
