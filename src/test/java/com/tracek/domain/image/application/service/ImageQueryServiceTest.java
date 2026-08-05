package com.tracek.domain.image.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.image.domain.exception.ImageErrorCode;
import com.tracek.domain.image.domain.model.Image;
import com.tracek.domain.image.domain.repository.ImageRepository;
import com.tracek.global.exception.CustomException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class ImageQueryServiceTest {

    @Mock private ImageRepository imageRepository;

    private ImageQueryService imageQueryService;

    @BeforeEach
    void setUp() {
        imageQueryService = new ImageQueryService(imageRepository);
    }

    @Test
    @DisplayName("존재하는 이미지 ID로 조회하면 ImageResult를 반환한다")
    void getImage_success() {
        Image image = Image.create("http://image.com/a.jpg");
        ReflectionTestUtils.setField(image, "id", 1L);
        given(imageRepository.findById(1L)).willReturn(Optional.of(image));

        ImageResult result = imageQueryService.getImage(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getImageUrl()).isEqualTo("http://image.com/a.jpg");
    }

    @Test
    @DisplayName("존재하지 않는 이미지 ID로 조회하면 IMAGE_NOT_FOUND 예외가 발생한다")
    void getImage_notFound() {
        given(imageRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> imageQueryService.getImage(999L))
                .isInstanceOf(CustomException.class)
                .extracting(e -> ((CustomException) e).getErrorCode())
                .isEqualTo(ImageErrorCode.IMAGE_NOT_FOUND);
    }
}
