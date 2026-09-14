package com.tracek.domain.content.infrastructure.persistence;

import com.tracek.domain.content.domain.model.ContentArtist;
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

    @Override
    public List<ContentArtist> findByArtistIdAndContentIds(Long artistId, List<Long> contentIds) {
        return contentArtistJpaRepository.findByArtistIdAndContentIdIn(artistId, contentIds);
    }

    @Override
    public List<ContentArtist> findFixedByContentId(Long contentId) {
        return contentArtistJpaRepository.findByContentIdAndIsFixedTrue(contentId);
    }

    @Override
    public List<ContentArtist> findByContentIds(List<Long> contentIds) {
        return contentArtistJpaRepository.findByContentIdIn(contentIds);
    }
}
