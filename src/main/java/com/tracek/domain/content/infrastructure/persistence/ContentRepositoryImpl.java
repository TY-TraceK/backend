package com.tracek.domain.content.infrastructure.persistence;

import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.repository.ContentRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentRepositoryImpl implements ContentRepository {
    private final ContentJpaRepository contentJpaRepository;

    @Override
    public Optional<Content> findById(Long id) {
        return contentJpaRepository.findById(id);
    }

    @Override
    public List<Content> findAllByIds(List<Long> ids) {
        return contentJpaRepository.findAllById(ids);
    }

    @Override
    public List<Content> findAll() {
        return contentJpaRepository.findAll();
    }

    @Override
    public Content save(Content content) {
        return contentJpaRepository.save(content);
    }

    @Override
    public void deleteById(Long id) {
        contentJpaRepository.deleteById(id);
    }
}
