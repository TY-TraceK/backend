package com.tracek.domain.content.infrastructure.persistence;

import com.tracek.domain.content.domain.repository.ContentArtistRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentArtistRepositoryImpl implements ContentArtistRepository {
    private final ContentArtistJpaRepository contentArtistJpaRepository;

    @Override
    public List<Long> findContentIdsByArtistId(Long artistId) {
        return contentArtistJpaRepository.findContentIdsByArtistId(artistId);
    }

    @Override
    public List<Long> findArtistIdsByContentId(Long contentId) {
        return contentArtistJpaRepository.findArtistIdsByContentId(contentId);
    }
}
