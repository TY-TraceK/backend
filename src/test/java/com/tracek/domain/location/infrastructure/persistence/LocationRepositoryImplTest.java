package com.tracek.domain.location.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.location.domain.model.GeoLocation;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationCategory;
import com.tracek.domain.location.domain.model.LocationContentArtist;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.global.common.vo.ImageUrl;
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

@ExtendWith(MockitoExtension.class)
class LocationRepositoryImplTest {

    @Mock private LocationJpaRepository locationJpaRepository;

    @Mock private LocationContentArtistJpaRepository locationContentArtistJpaRepository;

    private LocationRepositoryImpl locationRepositoryImpl;

    @BeforeEach
    void setUp() {
        locationRepositoryImpl =
                new LocationRepositoryImpl(
                        locationJpaRepository, locationContentArtistJpaRepository);
    }

    @Test
    @DisplayName("save/findById/findAll/deleteById는 LocationJpaRepository에 위임한다")
    void basicCrud_delegates() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        given(locationJpaRepository.save(location)).willReturn(location);
        given(locationJpaRepository.findById(1L)).willReturn(Optional.of(location));
        given(locationJpaRepository.findAll()).willReturn(List.of(location));

        assertThat(locationRepositoryImpl.save(location)).isEqualTo(location);
        assertThat(locationRepositoryImpl.findById(1L)).contains(location);
        assertThat(locationRepositoryImpl.findAll()).containsExactly(location);

        locationRepositoryImpl.deleteById(1L);
        verify(locationJpaRepository).deleteById(1L);
    }

    @Test
    @DisplayName("findNearbyLocations는 LocationJpaRepository에 위임한다")
    void findNearbyLocations_delegates() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        GeoLocation userLocation = GeoLocation.of(37.5, 127.0);
        given(locationJpaRepository.findNearbyLocations(userLocation, 1000.0))
                .willReturn(List.of(location));

        assertThat(locationRepositoryImpl.findNearbyLocations(userLocation, 1000.0))
                .containsExactly(location);
    }

    @Test
    @DisplayName("연관 콘텐츠-아티스트 매핑 조회는 LocationContentArtistJpaRepository에 위임한다")
    void relatedMappings_delegate() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        Content content =
                Content.create("궁궐 브이로그", "VARIETY", ImageUrl.from("http://image.com/c.jpg"));
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/a.jpg"), null, null);
        LocationContentArtist mapping = LocationContentArtist.create(location, content, artist);

        given(locationContentArtistJpaRepository.findByLocationId(1L)).willReturn(List.of(mapping));

        assertThat(locationRepositoryImpl.findRelatedContentAndArtists(1L))
                .containsExactly(mapping);
    }

    @Test
    @DisplayName("findByCategory/findAll(Pageable)은 LocationJpaRepository에 위임한다")
    void pagedQueries_delegate() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Location> page = new PageImpl<>(List.of(location), pageable, 1);

        given(locationJpaRepository.findByCategory(LocationCategory.ATTRACTION, pageable))
                .willReturn(page);
        given(locationJpaRepository.findAll(pageable)).willReturn(page);

        assertThat(locationRepositoryImpl.findByCategory(LocationCategory.ATTRACTION, pageable))
                .containsExactly(location);
        assertThat(locationRepositoryImpl.findAll(pageable)).containsExactly(location);
    }
}
