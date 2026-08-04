package com.tracek.global.common.vo;

import com.tracek.global.exception.CustomException;
import com.tracek.global.response.GeneralErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ImageUrl {
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    public static ImageUrl from(String imageUrl) {
        validateUrl(imageUrl);
        return new ImageUrl(imageUrl);
    }

    private static void validateUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank() || !imageUrl.startsWith("http")) {
            throw new CustomException(GeneralErrorCode.INVALID_INPUT_VALUE);
        }
    }
}
