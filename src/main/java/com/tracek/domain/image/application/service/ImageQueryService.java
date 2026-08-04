package com.tracek.domain.image.application.service;

import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.image.domain.exception.ImageErrorCode;
import com.tracek.domain.image.domain.model.Image;
import com.tracek.domain.image.domain.repository.ImageRepository;
import com.tracek.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ImageQueryService {
    private final ImageRepository imageRepository;

    // 타 도메인에서 이미지 정보가 필요할 때 참조하는 조회 메서드
    public ImageResult getImage(Long imageId) {
        Image image =
                imageRepository
                        .findById(imageId)
                        .orElseThrow(() -> new CustomException(ImageErrorCode.IMAGE_NOT_FOUND));
        return ImageResult.from(image);
    }
}
