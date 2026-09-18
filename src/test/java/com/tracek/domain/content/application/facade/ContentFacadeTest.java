package com.tracek.domain.content.application.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentDetailResult;
import com.tracek.domain.content.application.service.ContentArtistQueryService;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.Episode;
import com.tracek.domain.content.domain.model.EpisodeLocation;
import com.tracek.domain.fan.application.dto.result.ContentFanViewResult;
import com.tracek.domain.fan.application.service.FanQueryService;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import com.tracek.domain.ranking.application.dto.result.RankingSliceResult;
import com.tracek.domain.ranking.application.dto.result.RelatedLocationRankingResult;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
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
class ContentFacadeTest {

    @Mock private ContentQueryService contentQueryService;
    @Mock private ContentArtistQueryService contentArtistQueryService;
    @Mock private LocationQueryService locationQueryService;
    @Mock private ArtistQueryService artistQueryService;
    @Mock private EpisodeQueryService episodeQueryService;
    @Mock private VisitRankingQueryService visitRankingQueryService;
    @Mock private FanQueryService fanQueryService;

    private ContentFacade contentFacade;

    @BeforeEach
    void setUp() {
        contentFacade =
                new ContentFacade(
                        contentQueryService,
                        contentArtistQueryService,
                        locationQueryService,
                        artistQueryService,
                        episodeQueryService,
                        visitRankingQueryService,
                        fanQueryService);
    }

    @Test
    @DisplayName("콘텐츠 상세 조회 시 방문 인증 랭킹 순으로 정렬되고 회차 정보가 채워진다")
    void getContentDetails_success() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 1L);

        RankingCondition condition = new RankingCondition(null, null, 20);

        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 3L);

        Location location = LocationTestFixture.newLocation(2L, "경복궁", "ATTRACTION", 100L);
        Episode episode = Episode.create(content, "http://source.com", "1화", "2024-01-01", "note");
        EpisodeLocation episodeLocation = EpisodeLocation.create(episode, location);

        given(contentQueryService.getContentEntity(1L)).willReturn(content);
        given(contentArtistQueryService.findFixedArtistIdsByContentId(1L)).willReturn(List.of(3L));
        given(artistQueryService.getArtistsByIds(List.of(3L)))
                .willReturn(List.of(ArtistResult.from(artist)));
        given(visitRankingQueryService.getLocationsByContent(1L, condition))
                .willReturn(
                        new RankingSliceResult<>(
                                List.of(new RelatedLocationRankingResult(2L, 5L)),
                                null,
                                null,
                                false));
        given(locationQueryService.getLocationByIds(List.of(2L)))
                .willReturn(List.of(LocationResult.from(location)));
        given(episodeQueryService.getEpisodesByContentId(1L)).willReturn(List.of(episodeLocation));
        given(fanQueryService.getContentFanView(null, 1L))
                .willReturn(ContentFanViewResult.of(0L, false));

        ContentDetailResult result = contentFacade.getContentDetails(null, 1L, null, condition);

        assertThat(result.getContentInfo().getId()).isEqualTo(1L);
        assertThat(result.getContentInfo().getTitle()).isEqualTo("데뷔 앨범");
        assertThat(result.getContentInfo().getFixedArtists()).hasSize(1);
        assertThat(result.getContentInfo().getFixedArtists().get(0).getArtistName())
                .isEqualTo("아이유");
        assertThat(result.getLocations()).hasSize(1);
        assertThat(result.getLocations().get(0).getLocationName()).isEqualTo("경복궁");
        assertThat(result.getLocations().get(0).getRelatedVisitCount()).isEqualTo(5L);
        assertThat(result.getLocations().get(0).getEpisodeInfo()).hasSize(1);
    }

    @Test
    @DisplayName("연관 관광지/고정 아티스트가 없으면 빈 리스트로 조립된다")
    void getContentDetails_withoutMappings() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 1L);

        RankingCondition condition = new RankingCondition(null, null, 20);

        given(contentQueryService.getContentEntity(1L)).willReturn(content);
        given(contentArtistQueryService.findFixedArtistIdsByContentId(1L)).willReturn(List.of());
        given(artistQueryService.getArtistsByIds(List.of())).willReturn(List.of());
        given(visitRankingQueryService.getLocationsByContent(1L, condition))
                .willReturn(new RankingSliceResult<>(List.of(), null, null, false));
        given(episodeQueryService.getEpisodesByContentId(1L)).willReturn(List.of());
        given(fanQueryService.getContentFanView(null, 1L))
                .willReturn(ContentFanViewResult.of(0L, false));

        ContentDetailResult result = contentFacade.getContentDetails(null, 1L, null, condition);

        assertThat(result.getLocations()).isEmpty();
        assertThat(result.getContentInfo().getFixedArtists()).isEmpty();
    }

    @Test
    @DisplayName("콘텐츠 연관 장소 최신순 조회 시 리포지토리가 반환한 id 순서대로 재정렬된다")
    void getLatestLocationsByContent_success() {
        Location location2 = LocationTestFixture.newLocation(2L, "경복궁", "ATTRACTION", 100L);
        Location location4 = LocationTestFixture.newLocation(4L, "남산타워", "ATTRACTION", 100L);

        given(episodeQueryService.getLatestLocationIdsByContentId(1L, 2))
                .willReturn(List.of(4L, 2L));
        given(locationQueryService.getLocationByIds(List.of(4L, 2L)))
                .willReturn(
                        List.of(LocationResult.from(location2), LocationResult.from(location4)));

        List<LocationResult> result = contentFacade.getLatestLocationsByContent(1L, 2);

        assertThat(result).extracting(LocationResult::getLocationId).containsExactly(4L, 2L);
    }

    @Test
    @DisplayName("콘텐츠 연관 최신 장소가 없으면 빈 리스트를 반환한다")
    void getLatestLocationsByContent_withoutLocations() {
        given(episodeQueryService.getLatestLocationIdsByContentId(1L, 2)).willReturn(List.of());
        given(locationQueryService.getLocationByIds(List.of())).willReturn(List.of());

        List<LocationResult> result = contentFacade.getLatestLocationsByContent(1L, 2);

        assertThat(result).isEmpty();
    }
}
