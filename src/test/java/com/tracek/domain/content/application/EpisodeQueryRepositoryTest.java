package com.tracek.domain.content.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentArtistPair;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.Episode;
import com.tracek.domain.content.domain.model.EpisodeArtist;
import com.tracek.domain.content.domain.model.EpisodeLocation;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.global.common.vo.ImageUrl;
import com.tracek.global.config.QueryDslConfig;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Import({EpisodeQueryRepository.class, QueryDslConfig.class})
class EpisodeQueryRepositoryTest {

    @Autowired private EpisodeQueryRepository repository;

    @Autowired private TestEntityManager entityManager;

    private Artist artist;
    private Content content;
    private Location location;
    private Episode episode;

    @BeforeEach
    void setUp() {
        artist =
                entityManager.persistAndFlush(
                        Artist.create(
                                "아이유",
                                "IU",
                                ImageUrl.from("http://image.com/iu.jpg"),
                                null,
                                false));
        content =
                entityManager.persistAndFlush(
                        Content.create(
                                "궁궐 브이로그",
                                "ENTERTAINMENT",
                                "궁궐 브이로그 소개",
                                ImageUrl.from("http://image.com/c.jpg")));
        location =
                entityManager.persistAndFlush(
                        LocationTestFixture.newLocation(null, "경복궁", "ATTRACTION", 0L));
        episode =
                entityManager.persistAndFlush(
                        Episode.create(content, "http://source.com", "1화", "2024-01-01", "note"));
        entityManager.persistAndFlush(EpisodeLocation.create(episode, location));
        entityManager.persistAndFlush(EpisodeArtist.create(episode, artist));

        entityManager.clear();
    }

    @Test
    @DisplayName("아티스트-장소 연관 에피소드 조회 시 episode.content까지 fetch join 되어 예외 없이 조회된다")
    void getEpisodesIdsByArtistAndLocationIds_success() {
        List<EpisodeLocation> result =
                repository.getEpisodesIdsByArtistAndLocationIds(
                        artist.getId(), List.of(location.getId()));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getEpisode().getContent().getTitle()).isEqualTo("궁궐 브이로그");
    }

    @Test
    @DisplayName("아티스트-장소 연관 에피소드가 없으면 빈 리스트를 반환한다")
    void getEpisodesIdsByArtistAndLocationIds_empty() {
        List<EpisodeLocation> result =
                repository.getEpisodesIdsByArtistAndLocationIds(artist.getId(), List.of(-1L));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("아티스트-콘텐츠 연관 에피소드 조회 시 episode.content까지 fetch join 되어 예외 없이 조회된다")
    void getEpisodesByArtistAndContentIds_success() {
        List<EpisodeLocation> result =
                repository.getEpisodesByArtistAndContentIds(
                        artist.getId(), List.of(content.getId()));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getEpisode().getContent().getTitle()).isEqualTo("궁궐 브이로그");
    }

    @Test
    @DisplayName("아티스트-콘텐츠 연관 에피소드가 없으면 빈 리스트를 반환한다")
    void getEpisodesByArtistAndContentIds_empty() {
        List<EpisodeLocation> result =
                repository.getEpisodesByArtistAndContentIds(artist.getId(), List.of(-1L));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("장소 ID로 연관된 콘텐츠-아티스트 쌍을 조회한다")
    void getContentGroupsByLocationId_success() {
        List<ContentArtistPair> result = repository.getContentGroupsByLocationId(location.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getContentId()).isEqualTo(content.getId());
        assertThat(result.getFirst().getArtistId()).isEqualTo(artist.getId());
    }

    @Test
    @DisplayName("콘텐츠 ID로 연관된 장소 ID 목록을 조회한다")
    void getLocationIdsByContentId_success() {
        List<Long> result = repository.getLocationIdsByContentId(content.getId());

        assertThat(result).containsExactly(location.getId());
    }

    @Test
    @DisplayName("아티스트 ID로 연관된 장소 ID 목록을 조회한다")
    void getLocationIdsByArtistId_success() {
        List<Long> result = repository.getLocationIdsByArtistId(artist.getId());

        assertThat(result).containsExactly(location.getId());
    }

    @Test
    @DisplayName("장소-콘텐츠가 연관되어 있으면 true를 반환한다")
    void isRelatedContent_true() {
        assertThat(repository.isRelatedContent(location.getId(), content.getId())).isTrue();
    }

    @Test
    @DisplayName("장소-콘텐츠가 연관되어 있지 않으면 false를 반환한다")
    void isRelatedContent_false() {
        assertThat(repository.isRelatedContent(location.getId(), -1L)).isFalse();
    }

    @Test
    @DisplayName("장소-콘텐츠-아티스트가 모두 연관되어 있으면 true를 반환한다")
    void isRelatedContentAndArtist_true() {
        assertThat(
                        repository.isRelatedContentAndArtist(
                                location.getId(), content.getId(), artist.getId()))
                .isTrue();
    }

    @Test
    @DisplayName("장소-콘텐츠-아티스트 중 하나라도 연관되어 있지 않으면 false를 반환한다")
    void isRelatedContentAndArtist_false() {
        assertThat(repository.isRelatedContentAndArtist(location.getId(), content.getId(), -1L))
                .isFalse();
    }

    @Test
    @DisplayName("콘텐츠 ID로 연관 에피소드 장소 목록을 조회한다")
    void getEpisodesByContentId_success() {
        List<EpisodeLocation> result = repository.getEpisodesByContentId(content.getId());

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getLocation().getId()).isEqualTo(location.getId());
    }

    @Test
    @DisplayName("콘텐츠 -> 연관 장소 최신 등록순 조회 시 최대 size개만 반환한다")
    void getLatestLocationIdsByContentId_success() {
        List<Long> result = repository.getLatestLocationIdsByContentId(content.getId(), 10);

        assertThat(result).containsExactly(location.getId());
    }

    @Test
    @DisplayName("아티스트 -> 연관 장소 최신 등록순 조회 시 최대 size개만 반환한다")
    void getLatestLocationIdsByArtistId_success() {
        List<Long> result = repository.getLatestLocationIdsByArtistId(artist.getId(), 10);

        assertThat(result).containsExactly(location.getId());
    }
}
