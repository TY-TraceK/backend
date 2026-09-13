package com.tracek.domain.content.infrastructure.persistence;

import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.ContentCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ContentJpaRepository extends JpaRepository<Content, Long> {
    Page<Content> findByCategory(ContentCategory category, Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            "UPDATE Content c SET c.totalVerificationCount = c.totalVerificationCount + 1 WHERE c.id = :id")
    void increseVerificationCount(@Param("id") Long id);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            "UPDATE Content c SET c.totalVerificationCount = c.totalVerificationCount - 1 WHERE c.id = :id AND c.totalVerificationCount > 0")
    void decreseVerificationCount(@Param("id") Long id);
}
