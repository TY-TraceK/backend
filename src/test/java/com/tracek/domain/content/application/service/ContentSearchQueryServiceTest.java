package com.tracek.domain.content.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tracek.domain.content.application.ContentQueryRepository;
import com.tracek.domain.content.application.dto.ContentSearchQuery;
import com.tracek.domain.content.application.dto.ContentSearchResult;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentSearchQueryServiceTest {

    @Mock private ContentQueryRepository contentQueryRepository;

    private ContentSearchQueryService service;

    @BeforeEach
    void setUp() {
        service = new ContentSearchQueryService(contentQueryRepository);
    }

    private ContentSearchResult.ContentInfo info(long id) {
        return new ContentSearchResult.ContentInfo(
                id, "궁궐 브이로그", "ENTERTAINMENT", "http://image.com/c.jpg");
    }

    @Test
    @DisplayName("keyword가 없으면 빈 결과를 즉시 반환하고 리포지토리는 호출하지 않는다")
    void searchContents_blankKeyword_returnsEmptyWithoutCallingRepository() {
        ContentSearchQuery query = ContentSearchQuery.of(" ", null, 20);

        ContentSearchResult result = service.searchContents(query);

        assertThat(result.getContents()).isEmpty();
        verify(contentQueryRepository, never()).searchContents(any(), anyInt());
    }

    @Test
    @DisplayName("결과가 요청 size보다 많으면 hasNext가 true이고 size만큼만 반환한다")
    void searchContents_hasNext_whenMoreThanRequestedSize() {
        ContentSearchQuery query = ContentSearchQuery.of("궁궐", null, 1);
        given(contentQueryRepository.searchContents(query, 2))
                .willReturn(List.of(info(2L), info(1L)));

        ContentSearchResult result = service.searchContents(query);

        assertThat(result.getContents()).hasSize(1);
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getLastId()).isEqualTo(2L);
    }
}
