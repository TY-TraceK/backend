package com.tracek.domain.fan.domain.repository;

import com.tracek.domain.fan.domain.model.ArtistFan;
import com.tracek.domain.fan.domain.model.ArtistFanId;
import java.util.List;
import java.util.Optional;

public interface ArtistFanRepository {

    boolean existsById(ArtistFanId fanId);

    void saveAndFlush(ArtistFan artistFan);

    Optional<ArtistFan> findById(ArtistFanId fanId);

    void delete(ArtistFanId fanId);

    List<ArtistFan> findAllByUserId(Long userId);

    Integer countArtistFansByArtistId(Long artistId);

    Integer countArtistFansByUserId(Long userId);
}
