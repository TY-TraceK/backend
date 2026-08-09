package com.tracek.domain.image.domain.repository;

import com.tracek.domain.image.domain.model.Image;
import java.util.List;
import java.util.Optional;

public interface ImageRepository {
    Optional<Image> findById(Long id);

    List<Image> findAllByIds(List<Long> ids);

    List<Image> findAll();

    Image save(Image image);

    void deleteById(Long id);
}
