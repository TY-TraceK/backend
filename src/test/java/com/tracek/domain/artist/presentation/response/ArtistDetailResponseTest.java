package com.tracek.domain.artist.presentation.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.artist.application.dto.ArtistDetailResult;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.location.application.dto.LocationResult;
import com.tracek.domain.location.domain.model.Location;
import com.tracek.domain.location.domain.model.LocationTestFixture;
import com.tracek.global.common.vo.ImageUrl;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class ArtistDetailResponseTest {

    @Test
    @DisplayName("ArtistDetailResult를 ArtistDetailResponse로 변환하면 콘텐츠/관광지가 계층형으로 모두 매핑된다")
    void from_success() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 1L);
        ArtistDetailResult.ArtistInfo artistInfo = ArtistDetailResult.ArtistInfo.of(artist);

        Content content = Content.create("데뷔 앨범", "KPOP", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 2L);
        ContentResult contentResult = ContentResult.from(content);

        Location location = LocationTestFixture.newLocation(3L, "경복궁", "PALACE", 100L);
        LocationResult locationResult = LocationResult.from(location);

        ArtistDetailResult.ContentResult detailContentResult =
                ArtistDetailResult.ContentResult.of(
                        contentResult,
                        List.of(ArtistDetailResult.LocationResult.from(locationResult)));

        ArtistDetailResult result =
                ArtistDetailResult.from(artistInfo, List.of(detailContentResult));

        ArtistDetailResponse response = ArtistDetailResponse.from(result);

        assertThat(response.getArtistInfo().getId()).isEqualTo(1L);
        assertThat(response.getArtistInfo().getName()).isEqualTo("아이유");
        assertThat(response.getContents()).hasSize(1);
        assertThat(response.getContents().get(0).getContentTitle()).isEqualTo("데뷔 앨범");
        assertThat(response.getContents().get(0).getLocations()).hasSize(1);
        assertThat(response.getContents().get(0).getLocations().get(0).getLocationName())
                .isEqualTo("경복궁");
    }

    @Test
    @DisplayName("연관 콘텐츠가 없으면 빈 리스트로 변환된다")
    void from_withoutContents() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 1L);
        ArtistDetailResult result =
                ArtistDetailResult.from(ArtistDetailResult.ArtistInfo.of(artist), List.of());

        ArtistDetailResponse response = ArtistDetailResponse.from(result);

        assertThat(response.getContents()).isEmpty();
    }
}
