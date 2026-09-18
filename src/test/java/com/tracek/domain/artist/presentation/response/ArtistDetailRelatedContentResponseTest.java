package com.tracek.domain.artist.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tracek.domain.artist.application.dto.ArtistDetailRelatedContentResult;
import java.util.List;
import org.junit.jupiter.api.Test;

class ArtistDetailRelatedContentResponseTest {

    @Test
    void convertsNestedRelatedContentResult() {
        ArtistDetailRelatedContentResult.ArtistInfo artistInfo =
                mock(ArtistDetailRelatedContentResult.ArtistInfo.class);
        when(artistInfo.getId()).thenReturn(1L);
        when(artistInfo.getName()).thenReturn("artist");
        when(artistInfo.getRelatedArtists()).thenReturn(null);

        ArtistDetailRelatedContentResult.EpisodeResult episode =
                mock(ArtistDetailRelatedContentResult.EpisodeResult.class);
        when(episode.getEpisodeId()).thenReturn(2L);
        when(episode.getEpisodeInfo()).thenReturn("1화");
        when(episode.getEpisodeVisitDate()).thenReturn("2026-01-01");
        when(episode.getNote()).thenReturn("note");

        ArtistDetailRelatedContentResult.RelatedLocationResult location =
                mock(ArtistDetailRelatedContentResult.RelatedLocationResult.class);
        when(location.getLocationId()).thenReturn(3L);
        when(location.getLocationName()).thenReturn("location");
        when(location.getRelatedVisitCount()).thenReturn(4L);
        when(location.getEpisodeInfo()).thenReturn(List.of(episode));

        ArtistDetailRelatedContentResult.ContentResult content =
                mock(ArtistDetailRelatedContentResult.ContentResult.class);
        when(content.getContentId()).thenReturn(5L);
        when(content.getContentTitle()).thenReturn("content");
        when(content.getContentCategory()).thenReturn("DRAMA");
        when(content.getContentPictureUrl()).thenReturn("picture");
        when(content.getIsFixed()).thenReturn(true);
        when(content.getRelatedLocations()).thenReturn(List.of(location));

        ArtistDetailRelatedContentResult source =
                ArtistDetailRelatedContentResult.of(artistInfo, List.of(content));

        ArtistDetailRelatedContentResponse response =
                ArtistDetailRelatedContentResponse.from(source);

        assertThat(response.getArtistInfo().getId()).isEqualTo(1L);
        assertThat(response.getArtistInfo().getRelatedArtists()).isNull();
        assertThat(response.getContents()).hasSize(1);
        assertThat(response.getContents().getFirst().getContentId()).isEqualTo(5L);
        assertThat(response.getContents().getFirst().getRelatedLocations()).hasSize(1);
        assertThat(
                        response.getContents()
                                .getFirst()
                                .getRelatedLocations()
                                .getFirst()
                                .getEpisodeInfo())
                .hasSize(1);
    }

    @Test
    void preservesNullNestedCollections() {
        ArtistDetailRelatedContentResult.ArtistInfo artistInfo =
                mock(ArtistDetailRelatedContentResult.ArtistInfo.class);
        when(artistInfo.getRelatedArtists()).thenReturn(null);
        ArtistDetailRelatedContentResult.ContentResult content =
                mock(ArtistDetailRelatedContentResult.ContentResult.class);
        when(content.getRelatedLocations()).thenReturn(null);

        ArtistDetailRelatedContentResponse response =
                ArtistDetailRelatedContentResponse.from(
                        ArtistDetailRelatedContentResult.of(artistInfo, List.of(content)));

        assertThat(response.getArtistInfo().getRelatedArtists()).isNull();
        assertThat(response.getContents().getFirst().getRelatedLocations()).isNull();
    }
}
