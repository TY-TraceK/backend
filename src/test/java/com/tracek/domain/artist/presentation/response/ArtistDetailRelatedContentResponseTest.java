package com.tracek.domain.artist.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.artist.application.dto.ArtistDetailRelatedContentResult;
import com.tracek.domain.artist.application.dto.ArtistSummaryResult;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.dto.EpisodeResult;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.Episode;
import com.tracek.domain.fan.application.dto.result.ArtistFanViewResult;
import com.tracek.global.common.vo.ImageUrl;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ArtistDetailRelatedContentResponseTest {

    @Test
    @DisplayName("ArtistDetailRelatedContentResult를 Response로 변환하면 콘텐츠/연관 장소/회차/연관 아티스트가 모두 매핑된다")
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

        ArtistDetailRelatedContentResult.EpisodeResult episodeResult =
                ArtistDetailRelatedContentResult.EpisodeResult.from(EpisodeResult.from(episode));
        ArtistDetailRelatedContentResult.RelatedLocationResult relatedLocationResult =
                ArtistDetailRelatedContentResult.RelatedLocationResult.of(
                        4L, "경복궁", 5L, List.of(episodeResult));

        ArtistDetailRelatedContentResult.ContentResult contentResult =
                ArtistDetailRelatedContentResult.ContentResult.of(
                        ContentResult.from(content), true, List.of(relatedLocationResult));

        ArtistDetailRelatedContentResult.ArtistInfo artistInfo =
                ArtistDetailRelatedContentResult.ArtistInfo.of(
                        artist,
                        List.of(ArtistSummaryResult.from(relatedArtist)),
                        ArtistFanViewResult.of(10L, true));

        ArtistDetailRelatedContentResult result =
                ArtistDetailRelatedContentResult.of(artistInfo, List.of(contentResult));

        ArtistDetailRelatedContentResponse response =
                ArtistDetailRelatedContentResponse.from(result);

        assertThat(response.getArtistInfo().getId()).isEqualTo(1L);
        assertThat(response.getArtistInfo().getFanCount()).isEqualTo(10L);
        assertThat(response.getArtistInfo().getIsFan()).isTrue();
        assertThat(response.getArtistInfo().getRelatedArtists()).hasSize(1);
        assertThat(response.getArtistInfo().getRelatedArtists().getFirst().getName())
                .isEqualTo("웬디");

        assertThat(response.getContents()).hasSize(1);
        ArtistDetailRelatedContentResponse.ContentResponse contentResponse =
                response.getContents().getFirst();
        assertThat(contentResponse.getContentTitle()).isEqualTo("궁궐 브이로그");
        assertThat(contentResponse.getIsFixed()).isTrue();
        assertThat(contentResponse.getRelatedLocations()).hasSize(1);

        ArtistDetailRelatedContentResponse.RelatedLocationResponse relatedLocationResponse =
                contentResponse.getRelatedLocations().getFirst();
        assertThat(relatedLocationResponse.getLocationName()).isEqualTo("경복궁");
        assertThat(relatedLocationResponse.getRelatedVisitCount()).isEqualTo(5L);
        assertThat(relatedLocationResponse.getEpisodeInfo()).hasSize(1);
        assertThat(relatedLocationResponse.getEpisodeInfo().getFirst().getNote()).isEqualTo("note");
    }

    @Test
    @DisplayName("연관 콘텐츠/연관 아티스트가 없으면 빈 리스트로 변환된다")
    void from_withoutContentsAndRelatedArtists() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, false);
        ReflectionTestUtils.setField(artist, "id", 1L);

        ArtistDetailRelatedContentResult.ArtistInfo artistInfo =
                ArtistDetailRelatedContentResult.ArtistInfo.of(
                        artist, List.of(), ArtistFanViewResult.of(0L, false));
        ArtistDetailRelatedContentResult result =
                ArtistDetailRelatedContentResult.of(artistInfo, List.of());

        ArtistDetailRelatedContentResponse response =
                ArtistDetailRelatedContentResponse.from(result);

        assertThat(response.getArtistInfo().getFanCount()).isEqualTo(0L);
        assertThat(response.getArtistInfo().getIsFan()).isFalse();
        assertThat(response.getArtistInfo().getRelatedArtists()).isEmpty();
        assertThat(response.getContents()).isEmpty();
    }
}
