package com.tracek.domain.location.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.location.application.dto.LocationNearbyResult;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.application.dto.LocationSummaryResult;
import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationCategory;
import com.tracek.domain.location.domain.model.LocationContentArtist;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.domain.location.domain.repository.LocationRepository;
import com.tracek.global.common.vo.ImageUrl;
import com.tracek.global.exception.CustomException;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class LocationQueryServiceTest {

    @Mock private LocationRepository locationRepository;

    private LocationQueryService locationQueryService;

    @BeforeEach
    void setUp() {
        locationQueryService = new LocationQueryService(locationRepository);
    }

    @Test
    @DisplayName("존재하는 관광지 ID로 조회하면 Location 엔티티를 반환한다")
    void getLocationEntity_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(locationRepository.findById(1L)).willReturn(Optional.of(location));

        Location result = locationQueryService.getLocationEntity(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("경복궁");
    }

    @Test
    @DisplayName("존재하지 않는 관광지 ID로 조회하면 LOCATION_NOT_FOUND 예외가 발생한다")
    void getLocationEntity_notFound() {
        given(locationRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> locationQueryService.getLocationEntity(999L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.LOCATION_NOT_FOUND);
    }

    @Test
    @DisplayName("ID 목록으로 조회하면 배치로 LocationResult 목록을 반환한다")
    void getLocationByIds_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(locationRepository.findAllByIds(List.of(1L))).willReturn(List.of(location));

        List<LocationResult> results = locationQueryService.getLocationByIds(List.of(1L));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("경복궁");
    }

    @Test
    @DisplayName("ID 목록이 비어있으면 빈 리스트를 반환하고 조회하지 않는다")
    void getLocationByIds_empty() {
        List<LocationResult> results = locationQueryService.getLocationByIds(List.of());

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("주변 관광지를 거리순으로 조회한다")
    void getNearbyLocations_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
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
    @DisplayName("관광지 ID로 연관된 콘텐츠-아티스트 매핑을 조회한다")
    void getMappingsByLocationId_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        Content content =
                Content.create(
                        "궁궐 브이로그", "ENTERTAINMENT", ImageUrl.from("http://image.com/content.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        Artist artist =
                Artist.create(
                        "아이유", "IU", ImageUrl.from("http://image.com/artist.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 3L);
        LocationContentArtist mapping = LocationContentArtist.create(location, content, artist);

        given(locationRepository.findRelatedContentAndArtists(1L)).willReturn(List.of(mapping));

        List<LocationContentArtist> results = locationQueryService.getMappingsByLocationId(1L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getContent().getId()).isEqualTo(2L);
        assertThat(results.get(0).getArtist().getId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("콘텐츠 ID로 연관된 관광지-아티스트 매핑을 조회한다")
    void getMappingsByContentId_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        Content content =
                Content.create(
                        "궁궐 브이로그", "ENTERTAINMENT", ImageUrl.from("http://image.com/content.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        Artist artist =
                Artist.create(
                        "아이유", "IU", ImageUrl.from("http://image.com/artist.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 3L);
        LocationContentArtist mapping = LocationContentArtist.create(location, content, artist);

        given(locationRepository.findRelatedLocationAndArtists(2L)).willReturn(List.of(mapping));

        List<LocationContentArtist> results = locationQueryService.getMappingsByContentId(2L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLocation().getId()).isEqualTo(1L);
        assertThat(results.get(0).getArtist().getId()).isEqualTo(3L);
    }

    @Test
    @DisplayName("아티스트 ID로 연관된 관광지-콘텐츠 매핑을 조회한다")
    void getMappingByArtistId_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        Content content =
                Content.create(
                        "궁궐 브이로그", "ENTERTAINMENT", ImageUrl.from("http://image.com/content.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        Artist artist =
                Artist.create(
                        "아이유", "IU", ImageUrl.from("http://image.com/artist.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 3L);
        LocationContentArtist mapping = LocationContentArtist.create(location, content, artist);

        given(locationRepository.findRelatedLocationAndContents(3L)).willReturn(List.of(mapping));

        List<LocationContentArtist> results = locationQueryService.getMappingByArtistId(3L);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getLocation().getId()).isEqualTo(1L);
        assertThat(results.get(0).getContent().getId()).isEqualTo(2L);
    }

    @Test
    @DisplayName("카테고리를 지정하면 해당 카테고리의 관광지만 페이징 조회한다")
    void getLocationsByCategory_withCategory() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        Pageable pageable = PageRequest.of(0, 10);
        given(locationRepository.findByCategory(LocationCategory.ATTRACTION, pageable))
                .willReturn(new PageImpl<>(List.of(location), pageable, 1));

        Page<LocationSummaryResult> results =
                locationQueryService.getLocationsByCategory("ATTRACTION", pageable);

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getCategory())
                .isEqualTo(LocationCategory.ATTRACTION);
    }

    @Test
    @DisplayName("공백 카테고리로 조회하면 INVALID_CATEGORY 예외가 발생한다")
    void getLocationsByCategory_blankCategory() {
        Pageable pageable = PageRequest.of(0, 10);

        assertThatThrownBy(() -> locationQueryService.getLocationsByCategory(" ", pageable))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.INVALID_CATEGORY);
    }

    @Test
    @DisplayName("전체 관광지 목록을 페이징 조회한다")
    void getAllLocations_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        Pageable pageable = PageRequest.of(0, 10);
        given(locationRepository.findAll(pageable))
                .willReturn(new PageImpl<>(List.of(location), pageable, 1));

        Page<LocationSummaryResult> results = locationQueryService.getAllLocations(pageable);

        assertThat(results.getContent()).hasSize(1);
    }
}
