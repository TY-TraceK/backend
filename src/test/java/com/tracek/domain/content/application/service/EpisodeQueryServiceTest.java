package com.tracek.domain.content.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.content.application.EpisodeQueryRepository;
import com.tracek.domain.content.application.dto.ContentArtistPair;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.Episode;
import com.tracek.domain.content.domain.model.EpisodeLocation;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.global.common.vo.ImageUrl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class EpisodeQueryServiceTest {

    @Mock private EpisodeQueryRepository episodeQueryRepository;

    private EpisodeQueryService episodeQueryService;

    @BeforeEach
    void setUp() {
        episodeQueryService = new EpisodeQueryService(episodeQueryRepository);
    }

    @Test
    @DisplayName("관광지-콘텐츠 연관 여부를 리포지토리에 위임한다")
    void isRelatedContent_delegates() {
        given(episodeQueryRepository.isRelatedContent(1L, 2L)).willReturn(true);

        Boolean result = episodeQueryService.isRelatedContent(1L, 2L);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("연관되지 않은 관광지-콘텐츠는 false를 반환한다")
    void isRelatedContent_notRelated() {
        given(episodeQueryRepository.isRelatedContent(1L, 2L)).willReturn(false);

        Boolean result = episodeQueryService.isRelatedContent(1L, 2L);

        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("관광지-콘텐츠-아티스트 연관 여부를 리포지토리에 위임한다")
    void isRelatedContentAndArtist_delegates() {
        given(episodeQueryRepository.isRelatedContentAndArtist(1L, 2L, 3L)).willReturn(true);

        Boolean result = episodeQueryService.isRelatedContentAndArtist(1L, 2L, 3L);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("관광지 기준 콘텐츠-아티스트 쌍 목록 조회를 리포지토리에 위임한다")
    void getContentArtistPairs_delegates() {
        given(episodeQueryRepository.getContentGroupsByLocationId(1L))
                .willReturn(List.of(ContentArtistPair.of(10L, 20L)));

        assertThat(episodeQueryService.getContentArtistPairs(1L))
                .extracting(ContentArtistPair::getContentId)
                .containsExactly(10L);
    }

    @Test
    @DisplayName("콘텐츠 기준 관광지 id 목록 조회를 리포지토리에 위임한다")
    void getLocationIdsByContentId_delegates() {
        given(episodeQueryRepository.getLocationIdsByContentId(10L)).willReturn(List.of(1L, 2L));

        assertThat(episodeQueryService.getLocationIdsByContentId(10L)).containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("아티스트 기준 관광지 id 목록 조회를 리포지토리에 위임한다")
    void getLocationIdsByArtistId_delegates() {
        given(episodeQueryRepository.getLocationIdsByArtistId(1L)).willReturn(List.of(2L, 3L));

        assertThat(episodeQueryService.getLocationIdsByArtistId(1L)).containsExactly(2L, 3L);
    }

    @Test
    @DisplayName("아티스트-관광지 기준 회차 목록 조회를 리포지토리에 위임한다")
    void getEpisodesByArtistAndLocationIds_delegates() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        Episode episode = Episode.create(content, "http://source.com", "1화", "2024-01-01", "note");
        Location location = LocationTestFixture.newLocation(2L, "경복궁", "ATTRACTION", 100L);
        EpisodeLocation episodeLocation = EpisodeLocation.create(episode, location);

        given(episodeQueryRepository.getEpisodesIdsByArtistAndLocationIds(1L, List.of(2L)))
                .willReturn(List.of(episodeLocation));

        assertThat(episodeQueryService.getEpisodesByArtistAndLocationIds(1L, List.of(2L)))
                .containsExactly(episodeLocation);
    }

    @Test
    @DisplayName("아티스트-콘텐츠 기준 회차 목록 조회를 리포지토리에 위임한다")
    void getEpisodesByArtistAndContentIds_delegates() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        Episode episode = Episode.create(content, "http://source.com", "1화", "2024-01-01", "note");
        Location location = LocationTestFixture.newLocation(2L, "경복궁", "ATTRACTION", 100L);
        EpisodeLocation episodeLocation = EpisodeLocation.create(episode, location);
        ReflectionTestUtils.setField(content, "id", 10L);

        given(episodeQueryRepository.getEpisodesByArtistAndContentIds(1L, List.of(10L)))
                .willReturn(List.of(episodeLocation));

        assertThat(episodeQueryService.getEpisodesByArtistAndContentIds(1L, List.of(10L)))
                .containsExactly(episodeLocation);
    }

    @Test
    @DisplayName("콘텐츠 기준 회차 목록 조회를 리포지토리에 위임한다")
    void getEpisodesByContentId_delegates() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        Episode episode = Episode.create(content, "http://source.com", "1화", "2024-01-01", "note");
        Location location = LocationTestFixture.newLocation(2L, "경복궁", "ATTRACTION", 100L);
        EpisodeLocation episodeLocation = EpisodeLocation.create(episode, location);
        ReflectionTestUtils.setField(content, "id", 10L);

        given(episodeQueryRepository.getEpisodesByContentId(10L))
                .willReturn(List.of(episodeLocation));

        assertThat(episodeQueryService.getEpisodesByContentId(10L))
                .containsExactly(episodeLocation);
    }
}
