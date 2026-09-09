package com.tracek.domain.ranking.application.service.impl;

import com.tracek.domain.ranking.application.service.VisitRankingProjectionService;
import com.tracek.domain.ranking.domain.repository.ArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.LocationVisitRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitRankingProjectionServiceImpl implements VisitRankingProjectionService {

    private final LocationVisitRankingRepository locationVisitRankingRepository;
    private final ArtistLocationVisitRankingRepository artistLocationVisitRankingRepository;
    private final ContentLocationVisitRankingRepository contentLocationVisitRankingRepository;
    private final ContentArtistVisitRankingRepository contentArtistVisitRankingRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increase(Long locationId, Long contentId, Long artistId) {
        locationVisitRankingRepository.increaseVerificationCount(locationId);

        if (artistId != null) {
            artistLocationVisitRankingRepository.increaseVerificationCount(locationId, artistId);
        }

        if (contentId != null) {
            contentLocationVisitRankingRepository.increaseVerificationCount(locationId, contentId);
        }

        if (artistId != null && contentId != null) {
            contentArtistVisitRankingRepository.increaseVerificationCount(contentId, artistId);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(Long locationId, Long contentId, Long artistId) {
        locationVisitRankingRepository.decreaseVerificationCount(locationId);

        if (artistId != null) {
            artistLocationVisitRankingRepository.decreaseVerificationCount(locationId, artistId);
        }

        if (contentId != null) {
            contentLocationVisitRankingRepository.decreaseVerificationCount(locationId, contentId);
        }
        if (artistId != null && contentId != null) {
            contentArtistVisitRankingRepository.decreaseVerificationCount(contentId, artistId);
        }
    }
}
