package com.tracek.domain.artist.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tracek.domain.artist.application.ArtistQueryRepository;
import com.tracek.domain.artist.application.dto.ArtistSearchQuery;
import com.tracek.domain.artist.application.dto.ArtistSearchResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArtistSearchQueryServiceTest {

    @Mock private ArtistQueryRepository artistQueryRepository;

    private ArtistSearchQueryService service;

    @BeforeEach
    void setUp() {
        service = new ArtistSearchQueryService(artistQueryRepository);
    }

    private ArtistSearchResult.ArtistInfo info(long id) {
        return new ArtistSearchResult.ArtistInfo(
                id, "아이유", "IU", "http://image.com/iu.jpg", null, false);
    }

    @Test
    @DisplayName("keyword가 없으면 빈 결과를 즉시 반환하고 리포지토리는 호출하지 않는다")
    void searchArtists_blankKeyword_returnsEmptyWithoutCallingRepository() {
        ArtistSearchQuery query = ArtistSearchQuery.of(" ", null, 20);

        ArtistSearchResult result = service.searchArtists(query);

        assertThat(result.getArtists()).isEmpty();
        verify(artistQueryRepository, never()).searchArtists(any(), anyInt());
    }

    @Test
    @DisplayName("결과가 요청 size보다 많으면 hasNext가 true이고 size만큼만 반환한다")
    void searchArtists_hasNext_whenMoreThanRequestedSize() {
        ArtistSearchQuery query = ArtistSearchQuery.of("아이유", null, 1);
        given(artistQueryRepository.searchArtists(query, 2))
                .willReturn(List.of(info(2L), info(1L)));

        ArtistSearchResult result = service.searchArtists(query);

        assertThat(result.getArtists()).hasSize(1);
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getLastId()).isEqualTo(2L);
    }
}
