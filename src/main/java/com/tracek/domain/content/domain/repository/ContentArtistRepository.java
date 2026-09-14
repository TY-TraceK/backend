package com.tracek.domain.content.domain.repository;

import com.tracek.domain.content.domain.model.ContentArtist;
import java.util.List;

public interface ContentArtistRepository {
    List<Long> findContentIdsByArtistId(Long artistId);

    List<Long> findArtistIdsByContentId(Long contentId);

    List<ContentArtist> findByArtistIdAndContentIds(Long artistId, List<Long> contentIds);

    List<ContentArtist> findFixedByContentId(Long contentId);

    List<ContentArtist> findByContentIds(List<Long> contentIds);
}
