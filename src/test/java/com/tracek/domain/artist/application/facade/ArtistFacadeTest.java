package com.tracek.domain.artist.application.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.application.dto.ArtistDetailResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.service.ContentArtistQueryService;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.application.service.EpisodeQueryService;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.application.service.LocationQueryService;
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
class ArtistFacadeTest {

    @Mock private ArtistQueryService artistQueryService;
    @Mock private LocationQueryService locationQueryService;
    @Mock private ContentQueryService contentQueryService;
    @Mock private ContentArtistQueryService contentArtistQueryService;
    @Mock private EpisodeQueryService episodeQueryService;

    private ArtistFacade artistFacade;

    @BeforeEach
    void setUp() {
        artistFacade =
                new ArtistFacade(
                        artistQueryService,
                        locationQueryService,
                        contentQueryService,
                        contentArtistQueryService,
                        episodeQueryService);
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
}
