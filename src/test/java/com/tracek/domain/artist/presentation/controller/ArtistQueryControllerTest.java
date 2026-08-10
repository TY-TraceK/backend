package com.tracek.domain.artist.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.application.dto.ArtistDetailResult;
import com.tracek.domain.artist.application.dto.ArtistSummaryResult;
import com.tracek.domain.artist.application.facade.ArtistFacade;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.artist.presentation.response.ArtistDetailResponse;
import com.tracek.domain.artist.presentation.response.ArtistSummaryResponse;
import com.tracek.global.common.vo.ImageUrl;
import com.tracek.global.response.ApiResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ArtistQueryControllerTest {

    @Mock private ArtistFacade artistFacade;
    @Mock private ArtistQueryService artistQueryService;

    private ArtistQueryController controller;

    @BeforeEach
    void setUp() {
        controller = new ArtistQueryController(artistFacade, artistQueryService);
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

    @Test
    @DisplayName("아티스트 전체 목록을 페이징 응답으로 감싸서 반환한다")
    void getArtists_success() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 1L);
        Pageable pageable = PageRequest.of(0, 10);
        Page<ArtistSummaryResult> resultPage =
                new PageImpl<>(List.of(ArtistSummaryResult.from(artist)), pageable, 1);
        given(artistQueryService.getAllArtists(pageable)).willReturn(resultPage);

        ApiResponse<Page<ArtistSummaryResponse>> response = controller.getArtists(pageable);

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData().getContent()).hasSize(1);
        assertThat(response.getData().getContent().get(0).getName()).isEqualTo("아이유");
    }
}
