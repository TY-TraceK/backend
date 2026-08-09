package com.tracek.domain.content.infrastructure.persistence;

import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.ContentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentJpaRepository extends JpaRepository<Content, Long> {
    Page<Content> findByCategory(ContentCategory category, Pageable pageable);
}
