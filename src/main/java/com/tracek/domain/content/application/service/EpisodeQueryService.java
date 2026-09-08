package com.tracek.domain.content.application.service;

import com.tracek.domain.content.application.EpisodeQueryRepository;
import com.tracek.domain.content.application.dto.ContentArtistPair;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EpisodeQueryService {
    private final EpisodeQueryRepository episodeQueryRepository;

    public List<ContentArtistPair> getContentArtistPairs(Long locationId) {
        return episodeQueryRepository.getContentGroupsByLocationId(locationId);
    }

    public Boolean isRelatedContent(Long locationId, Long contentId) {
        return episodeQueryRepository.isRelatedContent(locationId, contentId);
    }

    public Boolean isRelatedContentAndArtist(Long locationId, Long contentId, Long artistId) {
        return episodeQueryRepository.isRelatedContentAndArtist(locationId, contentId, artistId);
    }
}
