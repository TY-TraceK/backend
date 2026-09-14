package com.tracek.domain.content.infrastructure.persistence;

import com.tracek.domain.content.domain.model.ContentArtist;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentArtistJpaRepository extends JpaRepository<ContentArtist, Long> {
    List<Long> findContentIdsByArtistId(Long artistId);

    List<Long> findArtistIdsByContentId(Long contentId);

    List<ContentArtist> findByArtistIdAndContentIdIn(Long artistId, List<Long> contentIds);

    List<ContentArtist> findByContentIdAndIsFixedTrue(Long contentId);

    List<ContentArtist> findByContentIdIn(List<Long> contentIds);
}
