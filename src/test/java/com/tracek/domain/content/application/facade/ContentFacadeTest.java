package com.tracek.domain.content.application.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.EpisodeQueryRepository;
import com.tracek.domain.content.application.dto.ContentDetailResult;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.repository.ContentArtistRepository;
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
class ContentFacadeTest {

    @Mock private ContentQueryService contentQueryService;
    @Mock private LocationQueryService locationQueryService;
    @Mock private ArtistQueryService artistQueryService;
    @Mock private EpisodeQueryRepository episodeQueryRepository;
    @Mock private ContentArtistRepository contentArtistRepository;

    private ContentFacade contentFacade;

    @BeforeEach
    void setUp() {
        contentFacade =
                new ContentFacade(
                        contentQueryService,
                        locationQueryService,
                        artistQueryService,
                        episodeQueryRepository,
                        contentArtistRepository);
    }

    @Test
    @DisplayName("콘텐츠 상세 조회 시 연관 관광지/아티스트가 플랫하게 조립된다")
    void getContentDetails_success() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 1L);

        Location location = LocationTestFixture.newLocation(2L, "경복궁", "ATTRACTION", 100L);
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 3L);

        given(contentQueryService.getContentEntity(1L)).willReturn(content);
        given(contentArtistRepository.findArtistIdsByContentId(1L)).willReturn(List.of(3L));
        given(episodeQueryRepository.getLocationIdsByContentId(1L)).willReturn(List.of(2L));
        given(locationQueryService.getLocationByIds(List.of(2L)))
                .willReturn(List.of(LocationResult.from(location)));
        given(artistQueryService.getArtistsByIds(List.of(3L)))
                .willReturn(List.of(ArtistResult.from(artist)));

        ContentDetailResult result = contentFacade.getContentDetails(1L);

        assertThat(result.getContentInfo().getId()).isEqualTo(1L);
        assertThat(result.getContentInfo().getTitle()).isEqualTo("데뷔 앨범");
        assertThat(result.getLocations()).hasSize(1);
        assertThat(result.getLocations().get(0).getLocationName()).isEqualTo("경복궁");
        assertThat(result.getArtists()).hasSize(1);
        assertThat(result.getArtists().get(0).getArtistName()).isEqualTo("아이유");
    }

    @Test
    @DisplayName("연관 관광지/아티스트가 없으면 빈 리스트로 조립된다")
    void getContentDetails_withoutMappings() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 1L);

        given(contentQueryService.getContentEntity(1L)).willReturn(content);
        given(contentArtistRepository.findArtistIdsByContentId(1L)).willReturn(List.of());
        given(episodeQueryRepository.getLocationIdsByContentId(1L)).willReturn(List.of());
        given(locationQueryService.getLocationByIds(List.of())).willReturn(List.of());
        given(artistQueryService.getArtistsByIds(List.of())).willReturn(List.of());

        ContentDetailResult result = contentFacade.getContentDetails(1L);

        assertThat(result.getLocations()).isEmpty();
        assertThat(result.getArtists()).isEmpty();
    }
}
