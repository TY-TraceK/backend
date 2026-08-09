package com.tracek.domain.image.infrastructure.persistence;

import com.tracek.domain.image.domain.model.Image;
import com.tracek.domain.image.domain.repository.ImageRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ImageRepositoryImpl implements ImageRepository {
    private final ImageJpaRepository imageJpaRepository;

    @Override
    public Optional<Image> findById(Long id) {
        return imageJpaRepository.findById(id);
    }

    @Override
    public List<Image> findAllByIds(List<Long> ids) {
        return imageJpaRepository.findAllById(ids);
    }

    @Override
    public List<Image> findAll() {
        return imageJpaRepository.findAll();
    }

    @Override
    public Image save(Image image) {
        return imageJpaRepository.save(image);
    }

    @Override
    public void deleteById(Long id) {
        imageJpaRepository.deleteById(id);
    }
}
