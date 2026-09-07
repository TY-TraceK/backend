package com.tracek.domain.ranking.application.service.impl;

import com.tracek.domain.ranking.application.service.VisitRankingProjectionService;
import com.tracek.domain.ranking.domain.model.ArtistLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.ContentLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.LocationVisitRanking;
import com.tracek.domain.ranking.domain.repository.ArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.LocationVisitRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitRankingProjectionServiceImpl
    implements VisitRankingProjectionService {

  private final LocationVisitRankingRepository locationVisitRankingRepository;
  private final ArtistLocationVisitRankingRepository artistLocationVisitRankingRepository;
  private final ContentLocationVisitRankingRepository contentLocationVisitRankingRepository;

  @Override
  public void increase(Long locationId, Long contentId, Long artistId) {
    increaseLocationRanking(locationId);

    if (artistId != null) {
      increaseArtistLocationRanking(locationId, artistId);
    }

    if (contentId != null) {
      increaseContentLocationRanking(locationId, contentId);
    }
  }

  @Override
  public void decrease(Long locationId, Long contentId, Long artistId) {
    decreaseLocationRanking(locationId);

    if (artistId != null) {
      decreaseArtistLocationRanking(locationId, artistId);
    }

    if (contentId != null) {
      decreaseContentLocationRanking(locationId, contentId);
    }
  }

  private void increaseLocationRanking(Long locationId) {
    LocationVisitRanking ranking =
        locationVisitRankingRepository
            .findByLocationId(locationId)
            .orElseGet(() ->
                locationVisitRankingRepository.save(
                    LocationVisitRanking.create(locationId)
                )
            );

    ranking.increaseVerificationCount();
  }

  private void increaseArtistLocationRanking(
      Long locationId,
      Long artistId
  ) {
    ArtistLocationVisitRanking ranking =
        artistLocationVisitRankingRepository
            .findByLocationIdAndArtistId(locationId, artistId)
            .orElseGet(() ->
                artistLocationVisitRankingRepository.save(
                    ArtistLocationVisitRanking.create(locationId, artistId)
                )
            );

    ranking.increaseVerificationCount();
  }

  private void increaseContentLocationRanking(
      Long locationId,
      Long contentId
  ) {
    ContentLocationVisitRanking ranking =
        contentLocationVisitRankingRepository
            .findByLocationIdAndContentId(locationId, contentId)
            .orElseGet(() ->
                contentLocationVisitRankingRepository.save(
                    ContentLocationVisitRanking.create(locationId, contentId)
                )
            );

    ranking.increaseVerificationCount();
  }

  private void decreaseLocationRanking(Long locationId) {
    locationVisitRankingRepository
        .findByLocationId(locationId)
        .ifPresent(LocationVisitRanking::decreaseVerificationCount);
  }

  private void decreaseArtistLocationRanking(
      Long locationId,
      Long artistId
  ) {
    artistLocationVisitRankingRepository
        .findByLocationIdAndArtistId(locationId, artistId)
        .ifPresent(ArtistLocationVisitRanking::decreaseVerificationCount);
  }

  private void decreaseContentLocationRanking(
      Long locationId,
      Long contentId
  ) {
    contentLocationVisitRankingRepository
        .findByLocationIdAndContentId(locationId, contentId)
        .ifPresent(ContentLocationVisitRanking::decreaseVerificationCount);
  }
}