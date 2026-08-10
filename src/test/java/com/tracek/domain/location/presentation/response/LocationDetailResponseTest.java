package com.tracek.domain.location.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.image.domain.model.Image;
import com.tracek.domain.location.application.dto.LocationDetailResult;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.global.common.vo.ImageUrl;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class LocationDetailResponseTest {

    @Test
    @DisplayName("LocationDetailResult를 LocationDetailResponse로 변환하면 이미지/콘텐츠/아티스트가 계층형으로 모두 매핑된다")
    void from_success() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        LocationDetailResult.LocationInfo locationInfo =
                LocationDetailResult.LocationInfo.of(location);

        Image image = Image.create("http://image.com/a.jpg");
        ReflectionTestUtils.setField(image, "id", 5L);
        LocationDetailResult.LocationImageResult imageResult =
                LocationDetailResult.LocationImageResult.of(ImageResult.from(image), true, 1);

        Content content =
                Content.create("궁궐 브이로그", "ENTERTAINMENT", ImageUrl.from("http://image.com/c.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        ContentResult contentResult = ContentResult.from(content);

        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/ar.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 3L);
        ArtistResult artistResult = ArtistResult.from(artist);

        LocationDetailResult.ContentResult detailContentResult =
                LocationDetailResult.ContentResult.of(
                        contentResult,
                        List.of(LocationDetailResult.ArtistResult.from(artistResult)));

        LocationDetailResult result =
                LocationDetailResult.from(
                        locationInfo, List.of(imageResult), List.of(detailContentResult));

        LocationDetailResponse response = LocationDetailResponse.from(result);

        assertThat(response.getLocationInfo().getLocationId()).isEqualTo(1L);
        assertThat(response.getLocationInfo().getName()).isEqualTo("경복궁");
        assertThat(response.getImages()).hasSize(1);
        assertThat(response.getImages().get(0).getImageUrl()).isEqualTo("http://image.com/a.jpg");
        assertThat(response.getContents()).hasSize(1);
        assertThat(response.getContents().get(0).getContentTitle()).isEqualTo("궁궐 브이로그");
        assertThat(response.getContents().get(0).getArtists()).hasSize(1);
        assertThat(response.getContents().get(0).getArtists().get(0).getArtistName())
                .isEqualTo("아이유");
    }

    @Test
    @DisplayName("연관 이미지/콘텐츠가 없으면 빈 리스트로 변환된다")
    void from_withoutRelatedItems() {
        Location location = LocationTestFixture.newLocation(1L, "경복궁", "ATTRACTION", 100L);
        LocationDetailResult result =
                LocationDetailResult.from(
                        LocationDetailResult.LocationInfo.of(location), List.of(), List.of());

        LocationDetailResponse response = LocationDetailResponse.from(result);

        assertThat(response.getImages()).isEmpty();
        assertThat(response.getContents()).isEmpty();
    }
}
