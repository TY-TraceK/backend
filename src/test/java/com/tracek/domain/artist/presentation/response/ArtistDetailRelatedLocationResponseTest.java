package com.tracek.domain.artist.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.artist.application.dto.ArtistDetailRelatedLocationResult;
import com.tracek.domain.artist.application.dto.ArtistSummaryResult;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.EpisodeResult;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.Episode;
import com.tracek.domain.fan.application.dto.result.ArtistFanViewResult;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.global.common.vo.ImageUrl;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ArtistDetailRelatedLocationResponseTest {

    @Test
    @DisplayName("ArtistDetailRelatedLocationResult를 Response로 변환하면 장소/회차/연관 아티스트가 모두 매핑된다")
    void from_success() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 1L);

        Artist relatedArtist =
                Artist.create(
                        "웬디", "WENDY", ImageUrl.from("http://image.com/wendy.jpg"), artist, false);
        ReflectionTestUtils.setField(relatedArtist, "id", 2L);

        Content content =
                Content.create(
                        "궁궐 브이로그", "ENTERTAINMENT", "소개", ImageUrl.from("http://image.com/c.jpg"));
        ReflectionTestUtils.setField(content, "id", 3L);
        Episode episode = Episode.create(content, "http://source.com", "1화", "2024-01-01", "note");

        Location location = LocationTestFixture.newLocation(4L, "경복궁", "ATTRACTION", 0L);

        ArtistDetailRelatedLocationResult.EpisodeResult episodeResult =
                ArtistDetailRelatedLocationResult.EpisodeResult.from(EpisodeResult.from(episode));
        ArtistDetailRelatedLocationResult.LocationResult locationResult =
                ArtistDetailRelatedLocationResult.LocationResult.of(
                        LocationResult.from(location), 5L, List.of(episodeResult));

        ArtistDetailRelatedLocationResult.ArtistInfo artistInfo =
                ArtistDetailRelatedLocationResult.ArtistInfo.of(
                        artist,
                        List.of(ArtistSummaryResult.from(relatedArtist)),
                        ArtistFanViewResult.of(10L, true));

        ArtistDetailRelatedLocationResult result =
                ArtistDetailRelatedLocationResult.of(artistInfo, List.of(locationResult));

        ArtistDetailRelatedLocationResponse response =
                ArtistDetailRelatedLocationResponse.from(result);

        assertThat(response.getArtistInfo().getId()).isEqualTo(1L);
        assertThat(response.getArtistInfo().getFanCount()).isEqualTo(10L);
        assertThat(response.getArtistInfo().getIsFan()).isTrue();
        assertThat(response.getArtistInfo().getRelatedArtists()).hasSize(1);
        assertThat(response.getArtistInfo().getRelatedArtists().getFirst().getName())
                .isEqualTo("웬디");

        assertThat(response.getLocations()).hasSize(1);
        ArtistDetailRelatedLocationResponse.LocationResponse locationResponse =
                response.getLocations().getFirst();
        assertThat(locationResponse.getLocationName()).isEqualTo("경복궁");
        assertThat(locationResponse.getRelatedVisitCount()).isEqualTo(5L);
        assertThat(locationResponse.getEpisodeInfo()).hasSize(1);
        assertThat(locationResponse.getEpisodeInfo().getFirst().getContentTitle())
                .isEqualTo("궁궐 브이로그");
    }

    @Test
    @DisplayName("연관 장소/연관 아티스트가 없으면 빈 리스트로 변환된다")
    void from_withoutLocationsAndRelatedArtists() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 1L);

        ArtistDetailRelatedLocationResult.ArtistInfo artistInfo =
                ArtistDetailRelatedLocationResult.ArtistInfo.of(
                        artist, List.of(), ArtistFanViewResult.of(0L, false));
        ArtistDetailRelatedLocationResult result =
                ArtistDetailRelatedLocationResult.of(artistInfo, List.of());

        ArtistDetailRelatedLocationResponse response =
                ArtistDetailRelatedLocationResponse.from(result);

        assertThat(response.getArtistInfo().getFanCount()).isEqualTo(0L);
        assertThat(response.getArtistInfo().getIsFan()).isFalse();
        assertThat(response.getArtistInfo().getRelatedArtists()).isEmpty();
        assertThat(response.getLocations()).isEmpty();
    }
}
