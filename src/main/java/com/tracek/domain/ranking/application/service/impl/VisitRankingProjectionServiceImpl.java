package com.tracek.domain.ranking.application.service.impl;

import com.tracek.domain.ranking.application.service.VisitRankingProjectionService;
import com.tracek.domain.ranking.domain.model.TargetId;
import com.tracek.domain.ranking.domain.repository.ArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.LocationVisitRankingRepository;
import java.util.Objects;
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
    private final ContentArtistLocationVisitRankingRepository
            contentArtistLocationVisitRankingRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void increase(Long locationId, Long contentId, Long artistId) {

        locationVisitRankingRepository.increaseVerificationCount(locationId);

        artistLocationVisitRankingRepository.increaseVerificationCount(locationId, artistId);

        contentLocationVisitRankingRepository.increaseVerificationCount(locationId, contentId);

        contentArtistVisitRankingRepository.increaseVerificationCount(contentId, artistId);

        contentArtistLocationVisitRankingRepository.increaseVerificationCount(
                new TargetId(locationId, contentId, artistId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decrease(Long locationId, Long contentId, Long artistId) {

        locationVisitRankingRepository.decreaseVerificationCount(locationId);

        artistLocationVisitRankingRepository.decreaseVerificationCount(locationId, artistId);

        contentLocationVisitRankingRepository.decreaseVerificationCount(locationId, contentId);

        contentArtistVisitRankingRepository.decreaseVerificationCount(contentId, artistId);

        contentArtistLocationVisitRankingRepository.decreaseVerificationCount(
                new TargetId(locationId, contentId, artistId));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void update(
            Long locationId,
            Long previousContentId,
            Long previousArtistId,
            Long updatedContentId,
            Long updatedArtistId) {

        boolean contentChanged = !Objects.equals(previousContentId, updatedContentId);

        boolean artistChanged = !Objects.equals(previousArtistId, updatedArtistId);

        if (!contentChanged && !artistChanged) {
            return;
        }

        if (contentChanged) {
            updateContentLocationRanking(locationId, previousContentId, updatedContentId);
        }

        if (artistChanged) {
            updateArtistLocationRanking(locationId, previousArtistId, updatedArtistId);
        }

        updateContentArtistRanking(
                previousContentId, previousArtistId, updatedContentId, updatedArtistId);

        updateContentArtistLocationRanking(
                locationId, previousContentId, previousArtistId, updatedContentId, updatedArtistId);
    }

    private void updateContentLocationRanking(
            Long locationId, Long previousContentId, Long updatedContentId) {

        contentLocationVisitRankingRepository.decreaseVerificationCount(
                locationId, previousContentId);

        contentLocationVisitRankingRepository.increaseVerificationCount(
                locationId, updatedContentId);
    }

    private void updateArtistLocationRanking(
            Long locationId, Long previousArtistId, Long updatedArtistId) {

        artistLocationVisitRankingRepository.decreaseVerificationCount(
                locationId, previousArtistId);

        artistLocationVisitRankingRepository.increaseVerificationCount(locationId, updatedArtistId);
    }

    private void updateContentArtistRanking(
            Long previousContentId,
            Long previousArtistId,
            Long updatedContentId,
            Long updatedArtistId) {

        contentArtistVisitRankingRepository.decreaseVerificationCount(
                previousContentId, previousArtistId);

        contentArtistVisitRankingRepository.increaseVerificationCount(
                updatedContentId, updatedArtistId);
    }

    private void updateContentArtistLocationRanking(
            Long locationId,
            Long previousContentId,
            Long previousArtistId,
            Long updatedContentId,
            Long updatedArtistId) {

        contentArtistLocationVisitRankingRepository.decreaseVerificationCount(
                new TargetId(locationId, previousContentId, previousArtistId));

        contentArtistLocationVisitRankingRepository.increaseVerificationCount(
                new TargetId(locationId, updatedContentId, updatedArtistId));
    }
}
