package com.tracek.domain.ranking.application.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.TargetId;
import com.tracek.domain.ranking.domain.repository.ArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistLocationVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentArtistVisitRankingRepository;
import com.tracek.domain.ranking.domain.repository.ContentLocationVisitRankingRepository;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class VisitRankingQueryServiceImplTest {

    @Mock private ContentArtistVisitRankingRepository contentArtistRankingRepository;

    @Mock private ContentLocationVisitRankingRepository contentLocationRankingRepository;

    @Mock private ArtistLocationVisitRankingRepository artistLocationRankingRepository;

    @Mock
    private ContentArtistLocationVisitRankingRepository contentArtistLocationVisitRankingRepository;

    private VisitRankingQueryServiceImpl service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        service =
                new VisitRankingQueryServiceImpl(
                        contentArtistRankingRepository,
                        contentLocationRankingRepository,
                        artistLocationRankingRepository,
                        contentArtistLocationVisitRankingRepository);
    }

    @Test
    void getArtistsByContent() {
        RankingCondition condition = new RankingCondition(null, null, 10);

        given(contentArtistRankingRepository.findArtistsByContent(1L, condition.toCriteria()))
                .willReturn(List.of(item(1L, 2L, 3L, 100L)));

        var result = service.getArtistsByContent(1L, condition);

        assertThat(result.rankings()).hasSize(1);
    }

    @Test
    void getLocationsByContent() {
        RankingCondition condition = new RankingCondition(null, null, 10);

        given(contentLocationRankingRepository.findLocationsByContent(1L, condition.toCriteria()))
                .willReturn(List.of(item(2L, 1L, null, 100L)));

        var result = service.getLocationsByContent(1L, condition);

        assertThat(result.rankings()).hasSize(1);
    }

    @Test
    void getLocationsByArtist() {
        RankingCondition condition = new RankingCondition(null, null, 10);

        given(artistLocationRankingRepository.findLocationsByArtist(3L, condition.toCriteria()))
                .willReturn(List.of(item(2L, null, 3L, 100L)));

        var result = service.getLocationsByArtist(3L, condition);

        assertThat(result.rankings()).hasSize(1);
    }

    @Test
    void getContentsByArtist() {
        RankingCondition condition = new RankingCondition(null, null, 10);

        given(contentArtistRankingRepository.findContentsByArtist(3L, condition.toCriteria()))
                .willReturn(List.of(item(null, 1L, 3L, 100L)));

        var result = service.getContentsByArtist(3L, condition);

        assertThat(result.rankings()).hasSize(1);
    }

    @Test
    void getArtistsByLocation() {
        RankingCondition condition = new RankingCondition(null, null, 10);

        given(artistLocationRankingRepository.findArtistsByLocation(2L, condition.toCriteria()))
                .willReturn(List.of(item(2L, null, 3L, 100L)));

        var result = service.getArtistsByLocation(2L, condition);

        assertThat(result.rankings()).hasSize(1);
    }

    @Test
    void getContentsByLocation() {
        RankingCondition condition = new RankingCondition(null, null, 10);

        given(contentLocationRankingRepository.findContentsByLocation(2L, condition.toCriteria()))
                .willReturn(List.of(item(2L, 1L, null, 100L)));

        var result = service.getContentsByLocation(2L, condition);

        assertThat(result.rankings()).hasSize(1);
    }

    @Test
    void getMultiRankingByContent() {
        given(contentArtistLocationVisitRankingRepository.findRankingsByContent(1L, null))
                .willReturn(List.of(item(2L, 1L, 3L, 100L)));

        RankingSliceResult<?> result = service.getMultiRankingByContent(1L);

        assertThat(result.rankings()).hasSize(1);
    }

    @Test
    void getMultiRankingByArtist() {
        given(contentArtistLocationVisitRankingRepository.findRankingsByArtist(3L, null))
                .willReturn(List.of(item(2L, 1L, 3L, 100L)));

        RankingSliceResult<?> result = service.getMultiRankingByArtist(3L);

        assertThat(result.rankings()).hasSize(1);
    }

    @Test
    void getMultiRankingByLocation() {
        given(contentArtistLocationVisitRankingRepository.findRankingsByLocation(2L, null))
                .willReturn(List.of(item(2L, 1L, 3L, 100L)));

        RankingSliceResult<?> result = service.getMultiRankingByLocation(2L);

        assertThat(result.rankings()).hasSize(1);
    }

    private RankingItem item(Long locationId, Long contentId, Long artistId, Long count) {

        return new RankingItem(new TargetId(locationId, contentId, artistId), count);
    }
}
