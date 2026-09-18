package com.tracek.domain.content.application.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tracek.domain.artist.application.dto.ArtistResult;
import java.util.List;
import org.junit.jupiter.api.Test;

class ContentDetailResultTest {

    @Test
    void createsFixedArtistLocationEpisodeAndTopLevelResult() {
        ArtistResult artist = mock(ArtistResult.class);
        when(artist.getId()).thenReturn(1L);
        when(artist.getName()).thenReturn("artist");
        when(artist.getPictureUrl()).thenReturn("artist-picture");
        ContentDetailResult.FixedArtistResult fixed =
                ContentDetailResult.FixedArtistResult.from(artist);

        com.tracek.domain.location.application.dto.LocationResult location =
                mock(com.tracek.domain.location.application.dto.LocationResult.class);
        when(location.getLocationId()).thenReturn(2L);
        when(location.getName()).thenReturn("location");
        when(location.getCategory()).thenReturn("ATTRACTION");
        when(location.getMainImageUrl()).thenReturn("location-picture");

        EpisodeResult episode = mock(EpisodeResult.class);
        when(episode.getId()).thenReturn(3L);
        when(episode.getEpisodeInfo()).thenReturn("1화");
        when(episode.getVisitDate()).thenReturn("2026-01-01");
        when(episode.getNote()).thenReturn("note");
        ContentDetailResult.EpisodeResult episodeResult =
                ContentDetailResult.EpisodeResult.from(episode);

        ContentDetailResult.LocationResult locationResult =
                ContentDetailResult.LocationResult.of(
                        location, 4L, List.of(episodeResult));
        ContentDetailResult.ContentInfo info = mock(ContentDetailResult.ContentInfo.class);
        ContentDetailResult result =
                ContentDetailResult.of(info, List.of(locationResult));

        assertThat(fixed.getArtistId()).isEqualTo(1L);
        assertThat(fixed.getArtistName()).isEqualTo("artist");
        assertThat(fixed.getArtistPictureUrl()).isEqualTo("artist-picture");
        assertThat(episodeResult.getEpisodeId()).isEqualTo(3L);
        assertThat(locationResult.getLocationId()).isEqualTo(2L);
        assertThat(locationResult.getRelatedVisitCount()).isEqualTo(4L);
        assertThat(result.getContentInfo()).isSameAs(info);
        assertThat(result.getLocations()).containsExactly(locationResult);
    }
}
