package com.tracek.domain.content.domain.repository;

import java.util.List;

public interface ContentArtistRepository {
    List<Long> findContentIdsByArtistId(Long artistId);

    List<Long> findArtistIdsByContentId(Long contentId);
}
