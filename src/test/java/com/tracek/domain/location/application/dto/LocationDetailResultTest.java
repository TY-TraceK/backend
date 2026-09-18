package com.tracek.domain.location.application.dto;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.image.application.dto.ImageResult;
import java.util.List;
import org.junit.jupiter.api.Test;

class LocationDetailResultTest {

    @Test
    void createsLocationImageResult() {
        ImageResult image = mock(ImageResult.class);
        when(image.getId()).thenReturn(11L);
        when(image.getImageUrl()).thenReturn("image-url");

        LocationDetailResult.LocationImageResult result =
                LocationDetailResult.LocationImageResult.of(image, true, 1);

        assertThat(result.getImageId()).isEqualTo(11L);
        assertThat(result.getImageUrl()).isEqualTo("image-url");
        assertThat(result.getIsMain()).isTrue();
        assertThat(result.getDisplayOrder()).isEqualTo(1);
    }

    @Test
    void createsContentResultWithAndWithoutVisitCount() {
        com.tracek.domain.content.application.dto.ContentResult content =
                mock(com.tracek.domain.content.application.dto.ContentResult.class);
        when(content.getContentId()).thenReturn(21L);
        when(content.getTitle()).thenReturn("content");
        when(content.getCategory()).thenReturn("DRAMA");
        when(content.getPictureUrl()).thenReturn("picture");

        LocationDetailResult.ContentResult withoutCount =
                LocationDetailResult.ContentResult.from(content);
        LocationDetailResult.ContentResult withCount =
                LocationDetailResult.ContentResult.of(content, 7L);

        assertThat(withoutCount.getContentId()).isEqualTo(21L);
        assertThat(withoutCount.getContentTitle()).isEqualTo("content");
        assertThat(withoutCount.getContentType()).isEqualTo("DRAMA");
        assertThat(withoutCount.getContentImageUrl()).isEqualTo("picture");
        assertThat(withoutCount.getRelatedVerificationsCount()).isNull();
        assertThat(withCount.getRelatedVerificationsCount()).isEqualTo(7L);
    }

    @Test
    void createsArtistResultWithAndWithoutVisitCount() {
        ArtistResult artist = mock(ArtistResult.class);
        when(artist.getId()).thenReturn(31L);
        when(artist.getName()).thenReturn("artist");
        when(artist.getPictureUrl()).thenReturn("picture");
        when(artist.getIsGroup()).thenReturn(false);

        LocationDetailResult.ArtistResult withoutCount =
                LocationDetailResult.ArtistResult.from(artist);
        LocationDetailResult.ArtistResult withCount =
                LocationDetailResult.ArtistResult.of(artist, 9L);

        assertThat(withoutCount.getArtistId()).isEqualTo(31L);
        assertThat(withoutCount.getArtistName()).isEqualTo("artist");
        assertThat(withoutCount.getArtistPictureUrl()).isEqualTo("picture");
        assertThat(withoutCount.getIsGroup()).isFalse();
        assertThat(withoutCount.getRelatedVerificationsCount()).isNull();
        assertThat(withCount.getRelatedVerificationsCount()).isEqualTo(9L);
    }

    @Test
    void createsTopLevelResult() {
        LocationDetailResult.LocationInfo info = mock(LocationDetailResult.LocationInfo.class);
        LocationDetailResult.LocationImageResult image =
                mock(LocationDetailResult.LocationImageResult.class);
        LocationDetailResult.ContentResult content = mock(LocationDetailResult.ContentResult.class);
        LocationDetailResult.ArtistResult artist = mock(LocationDetailResult.ArtistResult.class);

        LocationDetailResult result =
                LocationDetailResult.of(info, List.of(image), List.of(content), List.of(artist));

        assertThat(result.getLocationInfo()).isSameAs(info);
        assertThat(result.getImages()).containsExactly(image);
        assertThat(result.getContents()).containsExactly(content);
        assertThat(result.getArtists()).containsExactly(artist);
    }
}
