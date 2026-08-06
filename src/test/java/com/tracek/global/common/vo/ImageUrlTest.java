package com.tracek.global.common.vo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tracek.global.exception.CustomException;
import com.tracek.global.response.GeneralErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ImageUrlTest {

    @Test
    @DisplayName("http/https로 시작하는 URL이면 ImageUrl을 생성한다")
    void from_valid() {
        ImageUrl imageUrl = ImageUrl.from("https://image.com/a.jpg");

        assertThat(imageUrl.getImageUrl()).isEqualTo("https://image.com/a.jpg");
    }

    @Test
    @DisplayName("null이면 예외가 발생한다")
    void from_null() {
        assertThatThrownBy(() -> ImageUrl.from(null))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(GeneralErrorCode.INVALID_INPUT_VALUE);
    }

    @Test
    @DisplayName("http/https로 시작하지 않으면 예외가 발생한다")
    void from_invalidScheme() {
        assertThatThrownBy(() -> ImageUrl.from("ftp://image.com/a.jpg"))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(GeneralErrorCode.INVALID_INPUT_VALUE);
    }
}
