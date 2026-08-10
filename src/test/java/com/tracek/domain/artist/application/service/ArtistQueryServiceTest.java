package com.tracek.domain.artist.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.application.dto.ArtistSummaryResult;
import com.tracek.domain.artist.domain.exception.ArtistErrorCode;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.artist.domain.repository.ArtistRepository;
import com.tracek.global.common.vo.ImageUrl;
import com.tracek.global.exception.CustomException;
import java.util.List;
import java.util.Optional;
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
class ArtistQueryServiceTest {

    @Mock private ArtistRepository artistRepository;

    private ArtistQueryService artistQueryService;

    @BeforeEach
    void setUp() {
        artistQueryService = new ArtistQueryService(artistRepository);
    }

    @Test
    @DisplayName("존재하는 아티스트 ID로 조회하면 Artist 엔티티를 반환한다")
    void getArtistEntity_success() {
        Artist artist =
                Artist.create(
                        "아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), "가수 겸 배우", null);
        ReflectionTestUtils.setField(artist, "id", 1L);
        given(artistRepository.findById(1L)).willReturn(Optional.of(artist));

        Artist result = artistQueryService.getArtistEntity(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo("아이유");
        assertThat(result.getAlias()).isEqualTo("IU");
        assertThat(result.getPictureUrl().getImageUrl()).isEqualTo("http://image.com/iu.jpg");
        assertThat(result.getGroup()).isNull();
    }

    @Test
    @DisplayName("소속 그룹이 있는 아티스트는 group을 함께 반환한다")
    void getArtistEntity_withGroup() {
        Artist group =
                Artist.create("그룹", null, ImageUrl.from("http://image.com/g.jpg"), null, null);
        ReflectionTestUtils.setField(group, "id", 10L);

        Artist member =
                Artist.create("멤버", null, ImageUrl.from("http://image.com/m.jpg"), null, group);
        ReflectionTestUtils.setField(member, "id", 2L);
        given(artistRepository.findById(2L)).willReturn(Optional.of(member));

        Artist result = artistQueryService.getArtistEntity(2L);

        assertThat(result.getGroup().getId()).isEqualTo(10L);
    }

    @Test
    @DisplayName("존재하지 않는 아티스트 ID로 조회하면 ARTIST_NOT_FOUND 예외가 발생한다")
    void getArtistEntity_notFound() {
        given(artistRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> artistQueryService.getArtistEntity(999L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ArtistErrorCode.ARTIST_NOT_FOUND);
    }

    @Test
    @DisplayName("ID 목록으로 조회하면 배치로 ArtistResult 목록을 반환한다")
    void getArtistsByIds_success() {
        Artist artist =
                Artist.create(
                        "아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), "가수 겸 배우", null);
        ReflectionTestUtils.setField(artist, "id", 1L);
        given(artistRepository.findAllByIds(List.of(1L))).willReturn(List.of(artist));

        List<ArtistResult> results = artistQueryService.getArtistsByIds(List.of(1L));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("아이유");
    }

    @Test
    @DisplayName("ID 목록이 비어있으면 빈 리스트를 반환하고 조회하지 않는다")
    void getArtistsByIds_empty() {
        List<ArtistResult> results = artistQueryService.getArtistsByIds(List.of());

        assertThat(results).isEmpty();
    }

    @Test
    @DisplayName("전체 아티스트 목록을 페이징 조회한다")
    void getAllArtists_success() {
        Artist artist =
                Artist.create("아이유", "IU", ImageUrl.from("http://image.com/iu.jpg"), null, null);
        ReflectionTestUtils.setField(artist, "id", 1L);
        Pageable pageable = PageRequest.of(0, 10);
        given(artistRepository.findAll(pageable))
                .willReturn(new PageImpl<>(List.of(artist), pageable, 1));

        Page<ArtistSummaryResult> results = artistQueryService.getAllArtists(pageable);

        assertThat(results.getContent()).hasSize(1);
        assertThat(results.getContent().get(0).getName()).isEqualTo("아이유");
    }
}
