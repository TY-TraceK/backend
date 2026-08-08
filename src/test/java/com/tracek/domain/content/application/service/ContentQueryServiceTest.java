package com.tracek.domain.content.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.domain.exception.ContentErrorCode;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.repository.ContentRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ContentQueryServiceTest {

    @Mock private ContentRepository contentRepository;

    private ContentQueryService contentQueryService;

    @BeforeEach
    void setUp() {
        contentQueryService = new ContentQueryService(contentRepository);
    }

    @Test
    @DisplayName("존재하는 콘텐츠 ID로 조회하면 Content 엔티티를 반환한다")
    void getContentEntity_success() {
        Content content = Content.create("데뷔 앨범", "ALBUM", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 1L);
        given(contentRepository.findById(1L)).willReturn(Optional.of(content));

        Content result = contentQueryService.getContentEntity(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("데뷔 앨범");
        assertThat(result.getCategory()).isEqualTo("ALBUM");
        assertThat(result.getPictureUrl().getImageUrl()).isEqualTo("http://image.com/a.jpg");
    }

    @Test
    @DisplayName("존재하지 않는 콘텐츠 ID로 조회하면 CONTENT_NOT_FOUND 예외가 발생한다")
    void getContentEntity_notFound() {
        given(contentRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> contentQueryService.getContentEntity(999L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ContentErrorCode.CONTENT_NOT_FOUND);
    }

    @Test
    @DisplayName("ID 목록으로 조회하면 배치로 ContentResult 목록을 반환한다")
    void getContentsByIds_success() {
        Content content = Content.create("데뷔 앨범", "ALBUM", ImageUrl.from("http://image.com/a.jpg"));
        ReflectionTestUtils.setField(content, "id", 1L);
        given(contentRepository.findAllByIds(List.of(1L))).willReturn(List.of(content));

        List<ContentResult> results = contentQueryService.getContentsByIds(List.of(1L));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getTitle()).isEqualTo("데뷔 앨범");
    }

    @Test
    @DisplayName("ID 목록이 비어있으면 빈 리스트를 반환하고 조회하지 않는다")
    void getContentsByIds_empty() {
        List<ContentResult> results = contentQueryService.getContentsByIds(List.of());

        assertThat(results).isEmpty();
    }
}
