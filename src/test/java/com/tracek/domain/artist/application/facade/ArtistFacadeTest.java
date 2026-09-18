package com.tracek.domain.artist.application.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.application.dto.ArtistDetailRelatedContentResult;
import com.tracek.domain.artist.application.dto.ArtistDetailRelatedLocationResult;
import com.tracek.domain.artist.application.dto.ArtistDetailResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.service.ContentArtistQueryService;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.Episode;
import com.tracek.domain.content.domain.model.EpisodeLocation;
import com.tracek.domain.fan.application.dto.result.ArtistFanViewResult;
import com.tracek.domain.fan.application.service.FanQueryService;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.application.dto.result.RelatedContentRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedLocationRankingResult;
import com.tracek.domain.ranking.application.dto.result.RelatedMultiRankingResult;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
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
class ArtistFacadeTest {

    @Mock private ArtistQueryService artistQueryService;
    @Mock private LocationQueryService locationQueryService;
    @Mock private ContentQueryService contentQueryService;
    @Mock private ContentArtistQueryService contentArtistQueryService;
    @Mock private EpisodeQueryService episodeQueryService;
    @Mock private VisitRankingQueryService visitRankingQueryService;
    @Mock private FanQueryService fanQueryService;

    private ArtistFacade artistFacade;

    @BeforeEach
    void setUp() {
        artistFacade =
                new ArtistFacade(
                        artistQueryService,
                        locationQueryService,
                        contentQueryService,
                        contentArtistQueryService,
                        episodeQueryService,
                        visitRankingQueryService,
                        fanQueryService);
    }

    @Test
    @DisplayName("아티스트 상세 조회 시 연관 장소/콘텐츠가 플랫하게 조립된다")
    void getArtistDetails_success() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 1L);

        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        Location location = LocationTestFixture.newLocation(3L, "경복궁", "ATTRACTION", 100L);

        given(artistQueryService.getArtistEntity(1L)).willReturn(artist);
        given(contentArtistQueryService.findContentIdsByArtistId(1L)).willReturn(List.of(2L));
        given(episodeQueryService.getLocationIdsByArtistId(1L)).willReturn(List.of(3L));
        given(contentQueryService.getContentsByIds(List.of(2L)))
                .willReturn(List.of(ContentResult.from(content)));
        given(locationQueryService.getLocationByIds(List.of(3L)))
                .willReturn(List.of(LocationResult.from(location)));

        ArtistDetailResult result = artistFacade.getArtistDetails(1L);

        assertThat(result.getArtistInfo().getId()).isEqualTo(1L);
        assertThat(result.getArtistInfo().getName()).isEqualTo("아이유");
        assertThat(result.getContents()).hasSize(1);
        assertThat(result.getContents().get(0).getContentTitle()).isEqualTo("데뷔 앨범");
        assertThat(result.getLocations()).hasSize(1);
        assertThat(result.getLocations().get(0).getLocationName()).isEqualTo("경복궁");
    }

    @Test
    @DisplayName("연관 콘텐츠/장소가 없으면 빈 리스트로 조립된다")
    void getArtistDetails_withoutMappings() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 1L);

        given(artistQueryService.getArtistEntity(1L)).willReturn(artist);
        given(contentArtistQueryService.findContentIdsByArtistId(1L)).willReturn(List.of());
        given(episodeQueryService.getLocationIdsByArtistId(1L)).willReturn(List.of());
        given(contentQueryService.getContentsByIds(List.of())).willReturn(List.of());
        given(locationQueryService.getLocationByIds(List.of())).willReturn(List.of());

        ArtistDetailResult result = artistFacade.getArtistDetails(1L);

        assertThat(result.getContents()).isEmpty();
        assertThat(result.getLocations()).isEmpty();
    }

    @Test
    @DisplayName("관광지 탭 조회 시 방문 인증 랭킹 순으로 정렬되고 회차 정보가 채워진다")
    void getArtistDetailsRelatedLocation_success() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 1L);

        RankingCondition condition = new RankingCondition(null, null, 20);

        Location location = LocationTestFixture.newLocation(3L, "경복궁", "ATTRACTION", 100L);
        Content content =
                Content.create(
                        "궁궐 브이로그",
                        "ENTERTAINMENT",
                        "궁궐 브이로그 콘텐츠 소개",
                        ImageUrl.from("http://image.com/c.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        Episode episode = Episode.create(content, "http://source.com", "1화", "2024-01-01", "note");
        EpisodeLocation episodeLocation = EpisodeLocation.create(episode, location);

        given(artistQueryService.getArtistEntity(1L)).willReturn(artist);
        given(visitRankingQueryService.getLocationsByArtist(1L, condition))
                .willReturn(
                        new RankingSliceResult<>(
                                List.of(new RelatedLocationRankingResult(3L, 10L)),
                                null,
                                null,
                                false));
        given(locationQueryService.getLocationByIds(List.of(3L)))
                .willReturn(List.of(LocationResult.from(location)));
        given(episodeQueryService.getEpisodesByArtistAndLocationIds(1L, List.of(3L)))
                .willReturn(List.of(episodeLocation));
        given(fanQueryService.getArtistFanView(null, 1L))
                .willReturn(ArtistFanViewResult.of(0L, false));

        ArtistDetailRelatedLocationResult result =
                artistFacade.getArtistDetailsRelatedLocation(null, 1L, null, condition);

        assertThat(result.getArtistInfo().getId()).isEqualTo(1L);
        assertThat(result.getArtistInfo().getRelatedArtists()).isEmpty();
        assertThat(result.getLocations()).hasSize(1);
        assertThat(result.getLocations().get(0).getLocationName()).isEqualTo("경복궁");
        assertThat(result.getLocations().get(0).getRelatedVisitCount()).isEqualTo(10L);
        assertThat(result.getLocations().get(0).getEpisodeInfo()).hasSize(1);
        assertThat(result.getLocations().get(0).getEpisodeInfo().get(0).getContentTitle())
                .isEqualTo("궁궐 브이로그");
    }

    @Test
    @DisplayName("city로 필터링하면 해당 city가 아닌 장소는 제외된다")
    void getArtistDetailsRelatedLocation_filtersByCity() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 1L);

        RankingCondition condition = new RankingCondition(null, null, 20);
        Location location = LocationTestFixture.newLocation(3L, "경복궁", "ATTRACTION", 100L);

        given(artistQueryService.getArtistEntity(1L)).willReturn(artist);
        given(visitRankingQueryService.getLocationsByArtist(1L, condition))
                .willReturn(
                        new RankingSliceResult<>(
                                List.of(new RelatedLocationRankingResult(3L, 10L)),
                                null,
                                null,
                                false));
        given(locationQueryService.getLocationByIds(List.of(3L)))
                .willReturn(List.of(LocationResult.from(location)));
        given(episodeQueryService.getEpisodesByArtistAndLocationIds(1L, List.of(3L)))
                .willReturn(List.of());
        given(fanQueryService.getArtistFanView(null, 1L))
                .willReturn(ArtistFanViewResult.of(0L, false));

        ArtistDetailRelatedLocationResult result =
                artistFacade.getArtistDetailsRelatedLocation(null, 1L, "부산광역시", condition);

        assertThat(result.getLocations()).isEmpty();
    }

    @Test
    @DisplayName("콘텐츠 탭 조회 시 방문 인증 랭킹 순으로 정렬되고 장소/회차 정보가 채워진다")
    void getArtistDetailsRelatedContent_success() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 1L);

        RankingCondition condition = new RankingCondition(null, null, 20);

        Content content =
                Content.create(
                        "궁궐 브이로그",
                        "ENTERTAINMENT",
                        "궁궐 브이로그 콘텐츠 소개",
                        ImageUrl.from("http://image.com/c.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        Location location = LocationTestFixture.newLocation(3L, "경복궁", "ATTRACTION", 100L);
        Episode episode = Episode.create(content, "http://source.com", "1화", "2024-01-01", "note");
        EpisodeLocation episodeLocation = EpisodeLocation.create(episode, location);

        given(artistQueryService.getArtistEntity(1L)).willReturn(artist);
        given(visitRankingQueryService.getContentsByArtist(1L, condition))
                .willReturn(
                        new RankingSliceResult<>(
                                List.of(new RelatedContentRankingResult(2L, 8L)),
                                null,
                                null,
                                false));
        given(contentQueryService.getContentsByIds(List.of(2L)))
                .willReturn(List.of(ContentResult.from(content)));
        given(contentArtistQueryService.findIsFixedByArtistIdAndContentIds(1L, List.of(2L)))
                .willReturn(Map.of(2L, true));
        given(episodeQueryService.getEpisodesByArtistAndContentIds(1L, List.of(2L)))
                .willReturn(List.of(episodeLocation));
        given(locationQueryService.getLocationByIds(List.of(3L)))
                .willReturn(List.of(LocationResult.from(location)));
        given(visitRankingQueryService.getMultiRankingByArtist(1L))
                .willReturn(
                        new RankingSliceResult<>(
                                List.of(new RelatedMultiRankingResult(3L, 2L, 1L, 5L)),
                                null,
                                null,
                                false));
        given(fanQueryService.getArtistFanView(null, 1L))
                .willReturn(ArtistFanViewResult.of(0L, false));

        ArtistDetailRelatedContentResult result =
                artistFacade.getArtistDetailsRelatedContent(null, 1L, condition);

        assertThat(result.getArtistInfo().getId()).isEqualTo(1L);
        assertThat(result.getContents()).hasSize(1);
        assertThat(result.getContents().get(0).getContentTitle()).isEqualTo("궁궐 브이로그");
        assertThat(result.getContents().get(0).getIsFixed()).isTrue();
        assertThat(result.getContents().get(0).getRelatedLocations()).hasSize(1);
        assertThat(result.getContents().get(0).getRelatedLocations().get(0).getLocationName())
                .isEqualTo("경복궁");
        assertThat(result.getContents().get(0).getRelatedLocations().get(0).getRelatedVisitCount())
                .isEqualTo(5L);
        assertThat(result.getContents().get(0).getRelatedLocations().get(0).getEpisodeInfo())
                .hasSize(1);
    }

    @Test
    @DisplayName("콘텐츠 탭에서 연관 콘텐츠가 없으면 빈 리스트로 조립된다")
    void getArtistDetailsRelatedContent_withoutMappings() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 1L);

        RankingCondition condition = new RankingCondition(null, null, 20);

        given(artistQueryService.getArtistEntity(1L)).willReturn(artist);
        given(visitRankingQueryService.getContentsByArtist(1L, condition))
                .willReturn(new RankingSliceResult<>(List.of(), null, null, false));
        given(contentQueryService.getContentsByIds(List.of())).willReturn(List.of());
        given(contentArtistQueryService.findIsFixedByArtistIdAndContentIds(1L, List.of()))
                .willReturn(Map.of());
        given(episodeQueryService.getEpisodesByArtistAndContentIds(1L, List.of()))
                .willReturn(List.of());
        given(visitRankingQueryService.getMultiRankingByArtist(1L))
                .willReturn(new RankingSliceResult<>(List.of(), null, null, false));
        given(fanQueryService.getArtistFanView(null, 1L))
                .willReturn(ArtistFanViewResult.of(0L, false));

        ArtistDetailRelatedContentResult result =
                artistFacade.getArtistDetailsRelatedContent(null, 1L, condition);

        assertThat(result.getContents()).isEmpty();
    }

    @Test
    @DisplayName("아티스트 연관 장소 최신순 조회 시 리포지토리가 반환한 id 순서대로 재정렬된다")
    void getLatestLocationsByArtist_success() {
        Location location3 = LocationTestFixture.newLocation(3L, "경복궁", "ATTRACTION", 100L);
        Location location5 = LocationTestFixture.newLocation(5L, "남산타워", "ATTRACTION", 100L);

        given(episodeQueryService.getLatestLocationIdsByArtistId(1L, 2))
                .willReturn(List.of(5L, 3L));
        given(locationQueryService.getLocationByIds(List.of(5L, 3L)))
                .willReturn(
                        List.of(LocationResult.from(location3), LocationResult.from(location5)));

        List<LocationResult> result = artistFacade.getLatestLocationsByArtist(1L, 2);

        assertThat(result).extracting(LocationResult::getLocationId).containsExactly(5L, 3L);
    }

    @Test
    @DisplayName("아티스트 연관 최신 장소가 없으면 빈 리스트를 반환한다")
    void getLatestLocationsByArtist_withoutLocations() {
        given(episodeQueryService.getLatestLocationIdsByArtistId(1L, 2)).willReturn(List.of());
        given(locationQueryService.getLocationByIds(List.of())).willReturn(List.of());

        List<LocationResult> result = artistFacade.getLatestLocationsByArtist(1L, 2);

        assertThat(result).isEmpty();
    }
}
