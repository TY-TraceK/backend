package com.tracek.domain.content.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.ContentArtist;
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
class ContentArtistRepositoryImplTest {

    @Mock private ContentArtistJpaRepository contentArtistJpaRepository;

    private ContentArtistRepositoryImpl contentArtistRepositoryImpl;

    @BeforeEach
    void setUp() {
        contentArtistRepositoryImpl = new ContentArtistRepositoryImpl(contentArtistJpaRepository);
    }

    @Test
    @DisplayName("findContentIdsByArtistId는 ContentArtistJpaRepository에 위임한다")
    void findContentIdsByArtistId_delegates() {
        given(contentArtistJpaRepository.findContentIdsByArtistId(1L)).willReturn(List.of(10L));

        assertThat(contentArtistRepositoryImpl.findContentIdsByArtistId(1L)).containsExactly(10L);
    }

    @Test
    @DisplayName("findArtistIdsByContentId는 ContentArtistJpaRepository에 위임한다")
    void findArtistIdsByContentId_delegates() {
        given(contentArtistJpaRepository.findArtistIdsByContentId(10L)).willReturn(List.of(1L));

        assertThat(contentArtistRepositoryImpl.findArtistIdsByContentId(10L)).containsExactly(1L);
    }

    @Test
    @DisplayName(
            "findByArtistIdAndContentIds는 ContentArtistJpaRepository.findByArtistIdAndContentIdIn에 위임한다")
    void findByArtistIdAndContentIds_delegates() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ContentArtist contentArtist = ContentArtist.create(content, artist, true);

        given(contentArtistJpaRepository.findByArtistIdAndContentIdIn(1L, List.of(10L)))
                .willReturn(List.of(contentArtist));

        assertThat(contentArtistRepositoryImpl.findByArtistIdAndContentIds(1L, List.of(10L)))
                .containsExactly(contentArtist);
    }

    @Test
    @DisplayName(
            "findFixedByContentId는 ContentArtistJpaRepository.findByContentIdAndIsFixedTrue에 위임한다")
    void findFixedByContentId_delegates() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ContentArtist contentArtist = ContentArtist.create(content, artist, true);

        given(contentArtistJpaRepository.findByContentIdAndIsFixedTrue(10L))
                .willReturn(List.of(contentArtist));

        ReflectionTestUtils.setField(content, "id", 10L);

        assertThat(contentArtistRepositoryImpl.findFixedByContentId(10L))
                .containsExactly(contentArtist);
    }
}
