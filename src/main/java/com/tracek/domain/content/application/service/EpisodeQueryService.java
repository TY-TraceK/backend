package com.tracek.domain.content.application.service;

import com.tracek.domain.content.application.EpisodeQueryRepository;
import com.tracek.domain.content.application.dto.ContentArtistPair;
import com.tracek.domain.content.domain.model.EpisodeLocation;
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

    public List<Long> getLocationIdsByContentId(Long contentId) {
        return episodeQueryRepository.getLocationIdsByContentId(contentId);
    }

    public List<Long> getLocationIdsByArtistId(Long artistId) {
        return episodeQueryRepository.getLocationIdsByArtistId(artistId);
    }

    public Boolean isRelatedContent(Long locationId, Long contentId) {
        return episodeQueryRepository.isRelatedContent(locationId, contentId);
    }

    public Boolean isRelatedContentAndArtist(Long locationId, Long contentId, Long artistId) {
        return episodeQueryRepository.isRelatedContentAndArtist(locationId, contentId, artistId);
    }

    public List<EpisodeLocation> getEpisodesByArtistAndLocationIds(
            Long artistId, List<Long> locationIds) {
        return episodeQueryRepository.getEpisodesIdsByArtistAndLocationIds(artistId, locationIds);
    }

    public List<EpisodeLocation> getEpisodesByArtistAndContentIds(
            Long artistId, List<Long> contentIds) {
        return episodeQueryRepository.getEpisodesByArtistAndContentIds(artistId, contentIds);
    }

    public List<EpisodeLocation> getEpisodesByContentId(Long contentId) {
        return episodeQueryRepository.getEpisodesByContentId(contentId);
    }

    public List<Long> getLatestLocationIdsByContentId(Long contentId, int size) {
        return episodeQueryRepository.getLatestLocationIdsByContentId(contentId, size);
    }

    public List<Long> getLatestLocationIdsByArtistId(Long artistId, int size) {
        return episodeQueryRepository.getLatestLocationIdsByArtistId(artistId, size);
    }
}
