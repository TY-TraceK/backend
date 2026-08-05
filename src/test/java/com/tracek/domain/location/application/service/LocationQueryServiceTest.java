package com.tracek.domain.location.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
import com.tracek.domain.location.application.dto.LocationNearbyResult;
import com.tracek.domain.location.application.dto.LocationRelatedInfoResult;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.domain.location.domain.model.Address;
import com.tracek.domain.location.domain.model.GeoLocation;
import com.tracek.domain.location.domain.model.ImageLocation;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationContentArtist;
import com.tracek.domain.location.domain.repository.LocationRepository;
import com.tracek.global.common.vo.ImageUrl;
import com.tracek.global.exception.CustomException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LocationQueryServiceTest {

    @Mock private LocationRepository locationRepository;

    @Mock private ContentQueryService contentQueryService;

    @Mock private ArtistQueryService artistQueryService;

    @Mock private ImageQueryService imageQueryService;

    private LocationQueryService locationQueryService;

    @BeforeEach
    void setUp() {
        locationQueryService =
                new LocationQueryService(
                        locationRepository,
                        contentQueryService,
                        artistQueryService,
                        imageQueryService);
    }

    @Test
    @DisplayName("존재하는 관광지 ID로 조회하면 이미지/연관 콘텐츠/아티스트를 포함한 LocationResult를 반환한다")
    void getLocation_success() {
        Location location = newLocation(1L, "경복궁", "PALACE", 100L);
        Image image = Image.create("http://image.com/gyeongbok.jpg");
        ReflectionTestUtils.setField(image, "id", 5L);
        ImageLocation.create(location, image, 1, true);

        Content content =
                Content.create("궁궐 브이로그", "VARIETY", ImageUrl.from("http://image.com/content.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        Artist artist =
                Artist.create(
                        "아이유", "IU", ImageUrl.from("http://image.com/artist.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 3L);
        LocationContentArtist mapping = LocationContentArtist.create(location, content, artist);

        given(locationRepository.findById(1L)).willReturn(Optional.of(location));
        given(imageQueryService.getImage(5L))
                .willReturn(imageResultOf(5L, "http://image.com/gyeongbok.jpg"));
        given(locationRepository.findRelatedContentAndArtists(1L)).willReturn(List.of(mapping));
        given(contentQueryService.getContentsByIds(List.of(2L)))
                .willReturn(
                        List.of(
                                contentResultOf(
                                        2L, "궁궐 브이로그", "VARIETY", "http://image.com/content.jpg")));
        given(artistQueryService.getArtistsByIds(List.of(3L)))
                .willReturn(List.of(artistResultOf(3L, "아이유", "http://image.com/artist.jpg")));

        LocationResult result = locationQueryService.getLocation(1L);

        assertThat(result.getLocationId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("경복궁");
        assertThat(result.getImages()).hasSize(1);
        assertThat(result.getImages().get(0).getImageUrl())
                .isEqualTo("http://image.com/gyeongbok.jpg");
        assertThat(result.getImages().get(0).getIsMain()).isTrue();
        assertThat(result.getContents()).hasSize(1);
        assertThat(result.getContents().get(0).getTitle()).isEqualTo("궁궐 브이로그");
        assertThat(result.getArtists()).hasSize(1);
        assertThat(result.getArtists().get(0).getName()).isEqualTo("아이유");
    }

    @Test
    @DisplayName("존재하지 않는 관광지 ID로 조회하면 LOCATION_NOT_FOUND 예외가 발생한다")
    void getLocation_notFound() {
        given(locationRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> locationQueryService.getLocation(999L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.LOCATION_NOT_FOUND);
    }

    @Test
    @DisplayName("주변 관광지를 거리순으로 조회한다")
    void getNearbyLocations_success() {
        Location location = newLocation(1L, "경복궁", "PALACE", 100L);
        given(
                        locationRepository.findNearbyLocations(
                                org.mockito.ArgumentMatchers.any(),
                                org.mockito.ArgumentMatchers.eq(1000.0)))
                .willReturn(List.of(location));

        List<LocationNearbyResult> results =
                locationQueryService.getNearbyLocations(37.5665, 126.9780, 1000.0);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLocationId()).isEqualTo(1L);
        assertThat(results.get(0).getDistanceMeter()).isGreaterThanOrEqualTo(0.0);
    }

    @Test
    @DisplayName("관광지와 연관된 콘텐츠-아티스트 정보를 조회한다")
    void getRelatedContentAndArtists_success() {
        Location location = newLocation(1L, "경복궁", "PALACE", 100L);

        Content content =
                Content.create("궁궐 브이로그", "VARIETY", ImageUrl.from("http://image.com/content.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);

        Artist artist =
                Artist.create(
                        "아이유", "IU", ImageUrl.from("http://image.com/artist.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 3L);

        LocationContentArtist mapping = LocationContentArtist.create(location, content, artist);

        given(locationRepository.findById(1L)).willReturn(Optional.of(location));
        given(locationRepository.findRelatedContentAndArtists(1L)).willReturn(List.of(mapping));
        given(contentQueryService.getContentsByIds(List.of(2L)))
                .willReturn(
                        List.of(
                                contentResultOf(
                                        2L, "궁궐 브이로그", "VARIETY", "http://image.com/content.jpg")));
        given(artistQueryService.getArtistsByIds(List.of(3L)))
                .willReturn(List.of(artistResultOf(3L, "아이유", "http://image.com/artist.jpg")));

        LocationRelatedInfoResult result = locationQueryService.getRelatedContentAndArtists(1L);

        assertThat(result.getLocationId()).isEqualTo(1L);
        assertThat(result.getRelatedItems()).hasSize(1);
        assertThat(result.getRelatedItems().get(0).getContentTitle()).isEqualTo("궁궐 브이로그");
        assertThat(result.getRelatedItems().get(0).getArtistName()).isEqualTo("아이유");
    }

    // Location은 public 생성 팩토리가 없어 리플렉션으로 테스트 픽스처를 구성한다.
    private Location newLocation(Long id, String name, String category, Long likeCount) {
        try {
            Constructor<Location> constructor = Location.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            Location location = constructor.newInstance();
            ReflectionTestUtils.setField(location, "id", id);
            ReflectionTestUtils.setField(location, "name", name);
            ReflectionTestUtils.setField(location, "category", category);
            ReflectionTestUtils.setField(location, "likeCount", likeCount);
            ReflectionTestUtils.setField(
                    location, "address", Address.of("서울특별시", "종로구", "사직로 161"));
            ReflectionTestUtils.setField(
                    location, "geoLocation", GeoLocation.of(37.5796, 126.9770));
            return location;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    private ImageResult imageResultOf(Long id, String url) {
        Image image = Image.create(url);
        ReflectionTestUtils.setField(image, "id", id);
        return ImageResult.from(image);
    }

    private ContentResult contentResultOf(Long id, String title, String category, String url) {
        Content content = Content.create(title, category, ImageUrl.from(url));
        ReflectionTestUtils.setField(content, "id", id);
        return ContentResult.from(content);
    }

    private ArtistResult artistResultOf(Long id, String name, String url) {
        Artist artist = Artist.create(name, null, ImageUrl.from(url), null, null);
        ReflectionTestUtils.setField(artist, "id", id);
        return ArtistResult.from(artist);
    }
}
