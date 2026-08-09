package com.tracek.domain.image.application.service;

import com.tracek.domain.image.application.dto.ImageResult;
import com.tracek.domain.image.domain.exception.ImageErrorCode;
import com.tracek.domain.image.domain.model.Image;
import com.tracek.domain.image.domain.repository.ImageRepository;
import com.tracek.global.exception.CustomException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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

    // 여러 이미지 배치 조회 (N+1 방지)
    // findAllById는 존재하지 않는 ID를 결과에서 조용히 제외하므로, 누락된 ID가 있으면 예외로 명시한다.
    public List<ImageResult> getImagesByIds(List<Long> imageIds) {
        if (imageIds == null || imageIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Image> images = imageRepository.findAllByIds(imageIds);

        Set<Long> foundIds = images.stream().map(Image::getId).collect(Collectors.toSet());
        boolean hasMissingImage = imageIds.stream().anyMatch(id -> !foundIds.contains(id));
        if (hasMissingImage) {
            throw new CustomException(ImageErrorCode.IMAGE_NOT_FOUND);
        }

        return images.stream().map(ImageResult::from).toList();
    }
}
