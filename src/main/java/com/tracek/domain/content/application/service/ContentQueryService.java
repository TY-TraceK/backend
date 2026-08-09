package com.tracek.domain.content.application.service;

import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.dto.ContentSummaryResult;
import com.tracek.domain.content.domain.exception.ContentErrorCode;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.model.ContentCategory;
import com.tracek.domain.content.domain.repository.ContentRepository;
import com.tracek.global.exception.CustomException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContentQueryService {
    private final ContentRepository contentRepository;

    // 콘텐츠 Entity get
    public Content getContentEntity(Long contentId) {
        return contentRepository
                .findById(contentId)
                .orElseThrow(() -> new CustomException(ContentErrorCode.CONTENT_NOT_FOUND));
    }

    // 여러 콘텐츠 조회
    public List<ContentResult> getContentsByIds(List<Long> contentIds) {
        if (contentIds == null || contentIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Content> contents = contentRepository.findAllByIds(contentIds);

        return contents.stream().map(ContentResult::from).toList();
    }

    // 카테고리별 콘텐츠 목록 페이징 조회 (카테고리 미지정 시 전체 조회)
    public Page<ContentSummaryResult> getContentsByCategory(
            String categoryName, Pageable pageable) {
        ContentCategory category = ContentCategory.from(categoryName);

        Page<Content> contents =
                (category == null)
                        ? contentRepository.findAll(pageable)
                        : contentRepository.findByCategory(category, pageable);

        return contents.map(ContentSummaryResult::from);
    }
}
