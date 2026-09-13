package com.tracek.domain.content.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentDetailResult;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.Episode;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.global.common.vo.ImageUrl;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ContentDetailResponseTest {

    @Test
    @DisplayName("ContentDetailResult를 ContentDetailResponse로 변환하면 관광지/고정 아티스트가 모두 매핑된다")
    void from_success() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 1L);

        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 3L);
        ArtistResult artistResult = ArtistResult.from(artist);
        ContentDetailResult.FixedArtistResult fixedArtistResult =
                ContentDetailResult.FixedArtistResult.from(artistResult);

        ContentDetailResult.ContentInfo contentInfo =
                ContentDetailResult.ContentInfo.of(content, List.of(fixedArtistResult));

        Location location = LocationTestFixture.newLocation(2L, "경복궁", "ATTRACTION", 100L);
        LocationResult locationResult = LocationResult.from(location);
        ContentDetailResult.LocationResult detailLocationResult =
                ContentDetailResult.LocationResult.of(locationResult, 5L, List.of());

        ContentDetailResult result =
                ContentDetailResult.of(contentInfo, List.of(detailLocationResult));

        ContentDetailResponse response = ContentDetailResponse.from(result);

        assertThat(response.getContentInfo().getId()).isEqualTo(1L);
        assertThat(response.getContentInfo().getTitle()).isEqualTo("데뷔 앨범");
        assertThat(response.getContentInfo().getFixedArtists()).hasSize(1);
        assertThat(response.getContentInfo().getFixedArtists().get(0).getArtistName())
                .isEqualTo("아이유");
        assertThat(response.getLocations()).hasSize(1);
        assertThat(response.getLocations().get(0).getLocationName()).isEqualTo("경복궁");
        assertThat(response.getLocations().get(0).getRelatedVisitCount()).isEqualTo(5L);
    }

    @Test
    @DisplayName("연관 관광지/고정 아티스트가 없으면 빈 리스트로 변환된다")
    void from_withoutLocations() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 1L);
        ContentDetailResult result =
                ContentDetailResult.of(
                        ContentDetailResult.ContentInfo.of(content, List.of()), List.of());

        ContentDetailResponse response = ContentDetailResponse.from(result);

        assertThat(response.getLocations()).isEmpty();
        assertThat(response.getContentInfo().getFixedArtists()).isEmpty();
    }

    @Test
    @DisplayName("고정 아티스트/회차 정보가 null이면 응답에서도 null로 유지된다")
    void from_withNullFixedArtistsAndEpisodeInfo() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 1L);

        ContentDetailResult.ContentInfo contentInfo =
                ContentDetailResult.ContentInfo.of(content, null);

        Location location = LocationTestFixture.newLocation(2L, "경복궁", "ATTRACTION", 100L);
        LocationResult locationResult = LocationResult.from(location);
        ContentDetailResult.LocationResult detailLocationResult =
                ContentDetailResult.LocationResult.of(locationResult, 5L, null);

        ContentDetailResult result =
                ContentDetailResult.of(contentInfo, List.of(detailLocationResult));

        ContentDetailResponse response = ContentDetailResponse.from(result);

        assertThat(response.getContentInfo().getFixedArtists()).isNull();
        assertThat(response.getLocations().get(0).getEpisodeInfo()).isNull();
    }

    @Test
    @DisplayName("회차 정보가 있으면 EpisodeResponse로 변환된다")
    void from_withEpisodeInfo() {
        Content content =
                Content.create(
                        "데뷔 앨범", "KPOP", "데뷔 앨범 소개", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 1L);
        Episode episode = Episode.create(content, "http://source.com", "1화", "2024-01-01", "note");
        com.tracek.domain.content.application.dto.EpisodeResult episodeResult =
                com.tracek.domain.content.application.dto.EpisodeResult.from(episode);
        ContentDetailResult.EpisodeResult detailEpisodeResult =
                ContentDetailResult.EpisodeResult.from(episodeResult);

        Location location = LocationTestFixture.newLocation(2L, "경복궁", "ATTRACTION", 100L);
        LocationResult locationResult = LocationResult.from(location);
        ContentDetailResult.LocationResult detailLocationResult =
                ContentDetailResult.LocationResult.of(
                        locationResult, 5L, List.of(detailEpisodeResult));

        ContentDetailResult result =
                ContentDetailResult.of(
                        ContentDetailResult.ContentInfo.of(content, List.of()),
                        List.of(detailLocationResult));

        ContentDetailResponse response = ContentDetailResponse.from(result);

        assertThat(response.getLocations().get(0).getEpisodeInfo()).hasSize(1);
        assertThat(response.getLocations().get(0).getEpisodeInfo().get(0).getEpisodeInfo())
                .isEqualTo("1화");
        assertThat(response.getLocations().get(0).getEpisodeInfo().get(0).getNote())
                .isEqualTo("note");
    }
}
