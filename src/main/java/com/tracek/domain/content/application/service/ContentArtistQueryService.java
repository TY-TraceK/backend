package com.tracek.domain.content.application.service;

import com.tracek.domain.content.domain.repository.ContentArtistRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContentArtistQueryService {
    private final ContentArtistRepository contentArtistRepository;

    public List<Long> findContentIdsByArtistId(Long artistId) {
        return contentArtistRepository.findContentIdsByArtistId(artistId);
    }

    public List<Long> findArtistIdsByContentId(Long contentId) {
        return contentArtistRepository.findArtistIdsByContentId(contentId);
    }
}
