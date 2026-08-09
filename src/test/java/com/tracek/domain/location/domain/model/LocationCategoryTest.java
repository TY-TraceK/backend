package com.tracek.domain.location.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tracek.domain.location.domain.exception.LocationErrorCode;
import com.tracek.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LocationCategoryTest {

    @Test
    @DisplayName("정확히 일치하는 문자열이면 해당 카테고리로 변환한다")
    void from_exactMatch() {
        assertThat(LocationCategory.from("ATTRACTION")).isEqualTo(LocationCategory.ATTRACTION);
    }

    @Test
    @DisplayName("대소문자가 달라도 안전하게 변환한다")
    void from_caseInsensitive() {
        assertThat(LocationCategory.from("attraction")).isEqualTo(LocationCategory.ATTRACTION);
        assertThat(LocationCategory.from("Cafe")).isEqualTo(LocationCategory.CAFE);
    }

    @Test
    @DisplayName("null이나 빈 문자열이면 null을 반환한다")
    void from_nullOrBlank() {
        assertThat(LocationCategory.from(null)).isNull();
        assertThat(LocationCategory.from("")).isNull();
        assertThat(LocationCategory.from("   ")).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 카테고리면 INVALID_CATEGORY 예외가 발생한다")
    void from_invalid() {
        assertThatThrownBy(() -> LocationCategory.from("PALACE"))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(LocationErrorCode.INVALID_CATEGORY);
    }
}
