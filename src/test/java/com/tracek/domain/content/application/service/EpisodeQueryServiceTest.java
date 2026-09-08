package com.tracek.domain.content.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.content.application.EpisodeQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
}
