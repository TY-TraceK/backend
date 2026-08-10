package com.tracek.domain.content.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tracek.domain.content.domain.exception.ContentErrorCode;
import com.tracek.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ContentCategoryTest {

    @Test
    @DisplayName("정확히 일치하는 문자열이면 해당 카테고리로 변환한다")
    void from_exactMatch() {
        assertThat(ContentCategory.from("KPOP")).isEqualTo(ContentCategory.KPOP);
    }

    @Test
    @DisplayName("대소문자가 달라도 안전하게 변환한다")
    void from_caseInsensitive() {
        assertThat(ContentCategory.from("kpop")).isEqualTo(ContentCategory.KPOP);
        assertThat(ContentCategory.from("Drama")).isEqualTo(ContentCategory.DRAMA);
    }

    @Test
    @DisplayName("null이나 빈 문자열이면 null을 반환한다")
    void from_nullOrBlank() {
        assertThat(ContentCategory.from(null)).isNull();
        assertThat(ContentCategory.from("")).isNull();
        assertThat(ContentCategory.from("   ")).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 카테고리면 INVALID_CATEGORY 예외가 발생한다")
    void from_invalid() {
        assertThatThrownBy(() -> ContentCategory.from("ALBUM"))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ContentErrorCode.INVALID_CATEGORY);
    }
}
