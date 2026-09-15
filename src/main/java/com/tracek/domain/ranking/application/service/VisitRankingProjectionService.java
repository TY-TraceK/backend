package com.tracek.domain.ranking.application.service;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface VisitRankingProjectionService {

    void increase(Long locationId, Long contentId, Long artistId);

    void decrease(Long locationId, Long contentId, Long artistId);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void update(
            Long locationId,
            Long previousContentId,
            Long previousArtistId,
            Long updatedContentId,
            Long updatedArtistId);
}
