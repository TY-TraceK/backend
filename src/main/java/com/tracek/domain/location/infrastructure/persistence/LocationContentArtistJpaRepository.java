package com.tracek.domain.location.infrastructure.persistence;

import com.tracek.domain.location.domain.model.LocationContentArtist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationContentArtistJpaRepository
        extends JpaRepository<LocationContentArtist, Long> {

    // 해당 관광지와 연관된 콘텐츠-아티스트 매핑 조회
    List<LocationContentArtist> findByLocationId(Long locationId);

    // 해당 콘텐츠와 연관된 관광지-아티스트 매핑 조회
    List<LocationContentArtist> findByContentId(Long contentId);

    // 해당 아티스트와 연관된 관광지-콘텐츠 매핑 조회
    List<LocationContentArtist> findByArtistId(Long artistId);
}
