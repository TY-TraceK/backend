package com.tracek.domain.content.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.content.application.dto.ContentSearchQuery;
import com.tracek.domain.content.application.dto.ContentSearchResult;
import com.tracek.domain.content.application.service.ContentSearchQueryService;
import com.tracek.domain.content.presentation.request.ContentSearchRequest;
import com.tracek.domain.content.presentation.response.ContentSearchResponse;
import com.tracek.global.response.ApiResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentSearchControllerTest {

    @Mock private ContentSearchQueryService contentSearchQueryService;

    private ContentSearchController controller;

    @BeforeEach
    void setUp() {
        controller = new ContentSearchController(contentSearchQueryService);
    }

    @Test
    @DisplayName("검색 결과를 성공 응답으로 감싸서 반환한다")
    void searchContents_success() {
        ContentSearchRequest request = new ContentSearchRequest("궁궐 브이로그", null, 20);
        ContentSearchResult.ContentInfo info =
                new ContentSearchResult.ContentInfo(
                        1L, "궁궐 브이로그", "ENTERTAINMENT", "http://image.com/c.jpg");
        ContentSearchResult result = ContentSearchResult.of(List.of(info), 20);
        given(contentSearchQueryService.searchContents(any(ContentSearchQuery.class)))
                .willReturn(result);

        ApiResponse<ContentSearchResponse> response = controller.searchContents(request);

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData().getContents()).hasSize(1);
        assertThat(response.getData().getContents().get(0).getTitle()).isEqualTo("궁궐 브이로그");
    }
}
