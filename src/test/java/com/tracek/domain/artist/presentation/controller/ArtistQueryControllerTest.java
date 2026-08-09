package com.tracek.domain.artist.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.application.dto.ArtistDetailResult;
import com.tracek.domain.artist.application.facade.ArtistFacade;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.artist.presentation.response.ArtistDetailResponse;
import com.tracek.global.common.vo.ImageUrl;
import com.tracek.global.response.ApiResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ArtistQueryControllerTest {

    @Mock private ArtistFacade artistFacade;

    private ArtistQueryController controller;

    @BeforeEach
    void setUp() {
        controller = new ArtistQueryController(artistFacade);
    }

    @Test
    @DisplayName("아티스트 단건 상세 조회 성공 시 성공 응답으로 감싸서 반환한다")
    void getArtistDetails_success() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 1L);
        ArtistDetailResult result =
                ArtistDetailResult.from(ArtistDetailResult.ArtistInfo.of(artist), List.of());
        given(artistFacade.getArtistDetails(1L)).willReturn(result);

        ApiResponse<ArtistDetailResponse> response = controller.getArtistDetails(1L);

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData().getArtistInfo().getId()).isEqualTo(1L);
    }
}
