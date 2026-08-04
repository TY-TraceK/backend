package com.tracek.domain.content.domain.repository;

import com.tracek.domain.content.domain.model.Content;
import java.util.List;
import java.util.Optional;

public interface ContentRepository {
    Optional<Content> findById(Long id);

    List<Content> findAll();

    Content save(Content content);

    void deleteById(Long id);
}
