package com.tracek.domain.content.presentation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.content.application.dto.ContentDetailResult;
import com.tracek.domain.content.application.facade.ContentFacade;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.presentation.response.ContentDetailResponse;
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
class ContentQueryControllerTest {

    @Mock private ContentFacade contentFacade;

    private ContentQueryController controller;

    @BeforeEach
    void setUp() {
        controller = new ContentQueryController(contentFacade);
    }

    @Test
    @DisplayName("콘텐츠 단건 상세 조회 성공 시 성공 응답으로 감싸서 반환한다")
    void getContentDetails_success() {
        Content content = Content.create("데뷔 앨범", "KPOP", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 1L);
        ContentDetailResult result =
                ContentDetailResult.from(ContentDetailResult.ContentInfo.of(content), List.of());
        given(contentFacade.getContentDetails(1L)).willReturn(result);

        ApiResponse<ContentDetailResponse> response = controller.getContentDetails(1L);

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData().getContentInfo().getId()).isEqualTo(1L);
    }
}
