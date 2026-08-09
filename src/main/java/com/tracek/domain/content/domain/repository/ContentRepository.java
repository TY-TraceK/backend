package com.tracek.domain.content.domain.repository;

import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.ContentCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentRepository {
    Optional<Content> findById(Long id);

    List<Content> findAllByIds(List<Long> ids);

    List<Content> findAll();

    Content save(Content content);

    void deleteById(Long id);

    // 특정 카테고리의 콘텐츠 목록 페이징 조회
    Page<Content> findByCategory(ContentCategory category, Pageable pageable);

    Page<Content> findAll(Pageable pageable);
}
