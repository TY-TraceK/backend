package com.tracek.domain.ranking.application.service;

public interface VisitRankingProjectionService {

    void increase(Long locationId, Long contentId, Long artistId);

    void decrease(Long locationId, Long contentId, Long artistId);
}
