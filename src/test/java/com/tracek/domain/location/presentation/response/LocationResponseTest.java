package com.tracek.domain.location.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.image.domain.model.Image;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.global.common.vo.ImageUrl;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class LocationResponseTest {

    @Test
    @DisplayName("LocationResult를 LocationResponse로 변환하면 이미지/콘텐츠/아티스트가 모두 매핑된다")
    void from_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "PALACE", 100L);

        Image image = Image.create("http://image.com/a.jpg");
        ReflectionTestUtils.setField(image, "id", 5L);
        LocationResult.LocationImageResult imageResult =
                LocationResult.LocationImageResult.of(ImageResult.from(image), true, 1);

        Content content =
                Content.create("궁궐 브이로그", "VARIETY", ImageUrl.from("http://image.com/c.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);

        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/ar.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 3L);

        LocationResult result =
                LocationResult.of(
                        location,
                        List.of(imageResult),
                        List.of(ContentResult.from(content)),
                        List.of(ArtistResult.from(artist)));

        LocationResponse response = LocationResponse.from(result);

        assertThat(response.getLocationId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("경복궁");
        assertThat(response.getImages()).hasSize(1);
        assertThat(response.getImages().get(0).getImageUrl()).isEqualTo("http://image.com/a.jpg");
        assertThat(response.getContents()).hasSize(1);
        assertThat(response.getContents().get(0).getContentTitle()).isEqualTo("궁궐 브이로그");
        assertThat(response.getArtists()).hasSize(1);
        assertThat(response.getArtists().get(0).getArtistName()).isEqualTo("아이유");
    }

    @Test
    @DisplayName("연관 콘텐츠/아티스트가 없으면 빈 리스트로 변환된다")
    void from_withoutRelatedItems() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "PALACE", 100L);
        LocationResult result = LocationResult.from(location);

        LocationResponse response = LocationResponse.from(result);

        assertThat(response.getImages()).isEmpty();
        assertThat(response.getContents()).isEmpty();
        assertThat(response.getArtists()).isEmpty();
    }
}
