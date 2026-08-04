package com.tracek.domain.location.infrastructure.persistence;

import com.tracek.domain.location.domain.model.LocationContentArtist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface LocationContentArtistJpaRepository
        extends JpaRepository<LocationContentArtist, Long> {

    // 해당 관광지와 연관된 콘텐츠-아티스트 매핑 조회
    // 상세 정보(제목, 이름 등)는 ContentQueryService/ArtistQueryService를 통해 조회
    @Query("SELECT lca FROM LocationContentArtist lca WHERE lca.location.id = :locationId")
    List<LocationContentArtist> findByLocationId(Long locationId);
}
