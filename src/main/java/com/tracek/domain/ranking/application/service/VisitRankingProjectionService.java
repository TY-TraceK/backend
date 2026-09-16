package com.tracek.domain.ranking.application.service;

import java.util.Set;

public interface VisitRankingProjectionService {

    void increase(Long locationId, Long contentId, Set<Long> artistIds);

    void decrease(Long locationId, Long contentId, Set<Long> artistIds);

    void update(
            Long locationId,
            Long previousContentId,
            Set<Long> previousArtistIds,
            Long updatedContentId,
            Set<Long> updatedArtistIds);
}
