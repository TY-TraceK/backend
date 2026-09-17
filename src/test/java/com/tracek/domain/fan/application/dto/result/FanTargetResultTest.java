package com.tracek.domain.fan.application.dto.result;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.content.application.dto.ContentResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FanTargetResultTest {

    @Test
    @DisplayName("ContentResult를 FanTargetResult로 변환한다.")
    void fromContentResult_success() {
        // given
        ContentResult contentResult = mock(ContentResult.class);

        when(contentResult.getContentId()).thenReturn(10L);
        when(contentResult.getTitle()).thenReturn("런닝맨");
        when(contentResult.getDescription()).thenReturn("콘텐츠 설명");
        when(contentResult.getPictureUrl()).thenReturn("content.jpg");
        when(contentResult.getFanCount()).thenReturn(100L);
        when(contentResult.getTotalVerificationCount()).thenReturn(50L);

        // when
        FanTargetResult result = FanTargetResult.from(contentResult);

        // then
        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.name()).isEqualTo("런닝맨");
        assertThat(result.description()).isEqualTo("콘텐츠 설명");
        assertThat(result.pictureUrl()).isEqualTo("content.jpg");
        assertThat(result.fanCount()).isEqualTo(100L);
        assertThat(result.totalVerificationCount()).isEqualTo(50L);
    }

    @Test
    @DisplayName("ArtistResult를 FanTargetResult로 변환한다.")
    void fromArtistResult_success() {
        // given
        ArtistResult artistResult = mock(ArtistResult.class);

        when(artistResult.getId()).thenReturn(20L);
        when(artistResult.getName()).thenReturn("유재석");
        when(artistResult.getAlias()).thenReturn("국민 MC");
        when(artistResult.getPictureUrl()).thenReturn("artist.jpg");
        when(artistResult.getFanCount()).thenReturn(200L);
        when(artistResult.getTotalVerificationCount()).thenReturn(80L);

        // when
        FanTargetResult result = FanTargetResult.from(artistResult);

        // then
        assertThat(result.id()).isEqualTo(20L);
        assertThat(result.name()).isEqualTo("유재석");
        assertThat(result.description()).isEqualTo("국민 MC");
        assertThat(result.pictureUrl()).isEqualTo("artist.jpg");
        assertThat(result.fanCount()).isEqualTo(200L);
        assertThat(result.totalVerificationCount()).isEqualTo(80L);
    }

    @Test
    @DisplayName("아티스트 팬 조회 결과를 생성한다.")
    void artistFanViewResult_of() {
        // when
        ArtistFanViewResult result = ArtistFanViewResult.of(100L, true);

        // then
        assertThat(result.fanCount()).isEqualTo(100L);
        assertThat(result.isFan()).isTrue();
    }

    @Test
    @DisplayName("콘텐츠 팬 조회 결과를 생성한다.")
    void contentFanViewResult_of() {
        // when
        ContentFanViewResult result = ContentFanViewResult.of(200L, false);

        // then
        assertThat(result.fanCount()).isEqualTo(200L);
        assertThat(result.isFan()).isFalse();
    }
}
