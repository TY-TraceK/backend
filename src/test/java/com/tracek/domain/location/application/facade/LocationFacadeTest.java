package com.tracek.domain.location.application.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.image.application.service.ImageQueryService;
import com.tracek.domain.image.domain.model.Image;
import com.tracek.domain.location.application.dto.LocationDetailResult;
import com.tracek.domain.location.application.dto.LocationRelatedInfoResult;
import com.tracek.domain.location.application.service.LocationQueryService;
import com.tracek.domain.location.domain.model.ImageLocation;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationContentArtist;
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
class LocationFacadeTest {

    @Mock private LocationQueryService locationQueryService;
    @Mock private ContentQueryService contentQueryService;
    @Mock private ArtistQueryService artistQueryService;
    @Mock private ImageQueryService imageQueryService;

    private LocationFacade locationFacade;

    @BeforeEach
    void setUp() {
        locationFacade =
                new LocationFacade(
                        locationQueryService,
                        contentQueryService,
                        artistQueryService,
                        imageQueryService);
    }

    @Test
    @DisplayName("관광지 상세 조회 시 이미지/콘텐츠별 아티스트가 계층형으로 조립된다")
    void getLocationDetails_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "PALACE", 100L);
        Image image = Image.create("http://image.com/gyeongbok.jpg");
        ReflectionTestUtils.setField(image, "id", 5L);
        ImageLocation.create(location, image, 1, true);

        Content content =
                Content.create(
                        "궁궐 브이로그", "ENTERTAINMENT", ImageUrl.from("http://image.com/content.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        Artist artist =
                Artist.create(
                        "아이유", "IU", ImageUrl.from("http://image.com/artist.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 3L);
        LocationContentArtist mapping = LocationContentArtist.create(location, content, artist);

        given(locationQueryService.getLocationEntity(1L)).willReturn(location);
        given(imageQueryService.getImagesByIds(List.of(5L)))
                .willReturn(List.of(ImageResult.from(image)));
        given(locationQueryService.getMappingsByLocationId(1L)).willReturn(List.of(mapping));
        given(contentQueryService.getContentsByIds(List.of(2L)))
                .willReturn(List.of(ContentResult.from(content)));
        given(artistQueryService.getArtistsByIds(List.of(3L)))
                .willReturn(List.of(ArtistResult.from(artist)));

        LocationDetailResult result = locationFacade.getLocationDetails(1L);

        assertThat(result.getLocationInfo().getId()).isEqualTo(1L);
        assertThat(result.getLocationInfo().getName()).isEqualTo("경복궁");
        assertThat(result.getImages()).hasSize(1);
        assertThat(result.getImages().get(0).getImageUrl())
                .isEqualTo("http://image.com/gyeongbok.jpg");
        assertThat(result.getContents()).hasSize(1);
        assertThat(result.getContents().get(0).getContentTitle()).isEqualTo("궁궐 브이로그");
        assertThat(result.getContents().get(0).getArtists()).hasSize(1);
        assertThat(result.getContents().get(0).getArtists().get(0).getArtistName())
                .isEqualTo("아이유");
    }

    @Test
    @DisplayName("연관 콘텐츠/아티스트 매핑이 없으면 빈 리스트로 조립된다")
    void getLocationDetails_withoutMappings() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "PALACE", 100L);

        given(locationQueryService.getLocationEntity(1L)).willReturn(location);
        given(imageQueryService.getImagesByIds(List.of())).willReturn(List.of());
        given(locationQueryService.getMappingsByLocationId(1L)).willReturn(List.of());
        given(contentQueryService.getContentsByIds(List.of())).willReturn(List.of());
        given(artistQueryService.getArtistsByIds(List.of())).willReturn(List.of());

        LocationDetailResult result = locationFacade.getLocationDetails(1L);

        assertThat(result.getImages()).isEmpty();
        assertThat(result.getContents()).isEmpty();
    }

    @Test
    @DisplayName("관광지 관련 콘텐츠-아티스트 정보를 배치 조회로 조립한다")
    void getRelatedContentAndArtists_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "PALACE", 100L);

        Content content =
                Content.create(
                        "궁궐 브이로그", "ENTERTAINMENT", ImageUrl.from("http://image.com/content.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        Artist artist =
                Artist.create(
                        "아이유", "IU", ImageUrl.from("http://image.com/artist.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 3L);
        LocationContentArtist mapping = LocationContentArtist.create(location, content, artist);

        given(locationQueryService.getLocationEntity(1L)).willReturn(location);
        given(locationQueryService.getMappingsByLocationId(1L)).willReturn(List.of(mapping));
        given(contentQueryService.getContentsByIds(List.of(2L)))
                .willReturn(List.of(ContentResult.from(content)));
        given(artistQueryService.getArtistsByIds(List.of(3L)))
                .willReturn(List.of(ArtistResult.from(artist)));

        LocationRelatedInfoResult result = locationFacade.getRelatedContentAndArtists(1L);

        assertThat(result.getLocationId()).isEqualTo(1L);
        assertThat(result.getRelatedItems()).hasSize(1);
        assertThat(result.getRelatedItems().get(0).getContentTitle()).isEqualTo("궁궐 브이로그");
        assertThat(result.getRelatedItems().get(0).getArtistName()).isEqualTo("아이유");
    }
}
