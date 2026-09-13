package com.tracek.domain.content.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.ContentArtist;
import com.tracek.domain.content.domain.repository.ContentArtistRepository;
import com.tracek.global.common.vo.ImageUrl;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ContentArtistQueryServiceTest {

    @Mock private ContentArtistRepository contentArtistRepository;

    private ContentArtistQueryService contentArtistQueryService;

    @BeforeEach
    void setUp() {
        contentArtistQueryService = new ContentArtistQueryService(contentArtistRepository);
    }

    @Test
    @DisplayName("아티스트 id로 연관 콘텐츠 id 목록 조회를 리포지토리에 위임한다")
    void findContentIdsByArtistId_delegates() {
        given(contentArtistRepository.findContentIdsByArtistId(1L)).willReturn(List.of(10L, 20L));

        assertThat(contentArtistQueryService.findContentIdsByArtistId(1L))
                .containsExactly(10L, 20L);
    }

    @Test
    @DisplayName("콘텐츠 id로 연관 아티스트 id 목록 조회를 리포지토리에 위임한다")
    void findArtistIdsByContentId_delegates() {
        given(contentArtistRepository.findArtistIdsByContentId(10L)).willReturn(List.of(1L, 2L));

        assertThat(contentArtistQueryService.findArtistIdsByContentId(10L)).containsExactly(1L, 2L);
    }

    @Test
    @DisplayName("아티스트-콘텐츠별 고정 출연 여부를 contentId 기준 맵으로 조립한다")
    void findIsFixedByArtistIdAndContentIds_success() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 10L);
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 1L);
        ContentArtist contentArtist = ContentArtist.create(content, artist, true);

        given(contentArtistRepository.findByArtistIdAndContentIds(1L, List.of(10L)))
                .willReturn(List.of(contentArtist));

        Map<Long, Boolean> result =
                contentArtistQueryService.findIsFixedByArtistIdAndContentIds(1L, List.of(10L));

        assertThat(result).containsEntry(10L, true);
    }

    @Test
    @DisplayName("콘텐츠의 고정 출연 아티스트 id 목록을 조립한다")
    void findFixedArtistIdsByContentId_success() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 10L);
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 1L);
        ContentArtist contentArtist = ContentArtist.create(content, artist, true);

        given(contentArtistRepository.findFixedByContentId(10L)).willReturn(List.of(contentArtist));

        assertThat(contentArtistQueryService.findFixedArtistIdsByContentId(10L))
                .containsExactly(1L);
    }
}
