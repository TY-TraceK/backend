package com.tracek.domain.artist.application.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.global.common.vo.ImageUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ArtistDetailResultTest {

    @Test
    @DisplayName("pictureUrl이 있으면 문자열로 매핑된다")
    void artistInfoOf_withPictureUrl() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 1L);

        ArtistDetailResult.ArtistInfo artistInfo = ArtistDetailResult.ArtistInfo.of(artist);

        assertThat(artistInfo.getPictureUrl()).isEqualTo("http://image.com/iu.jpg");
    }

    @Test
    @DisplayName("pictureUrl이 null이면 NPE 없이 null로 매핑된다")
    void artistInfoOf_withoutPictureUrl() {
        Artist artist = Artist.create("아이유", "IU", null, null, null);
        ReflectionTestUtils.setField(artist, "id", 1L);

        ArtistDetailResult.ArtistInfo artistInfo = ArtistDetailResult.ArtistInfo.of(artist);

        assertThat(artistInfo.getPictureUrl()).isNull();
    }
}
