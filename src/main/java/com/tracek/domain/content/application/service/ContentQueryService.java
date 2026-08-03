package com.tracek.domain.content.application.service;

import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.domain.exception.ContentErrorCode;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.content.domain.repository.ContentRepository;
import com.tracek.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContentQueryService {
    private final ContentRepository contentRepository;

    // 타 도메인에서 콘텐츠 정보가 필요할 때 참조하는 조회 메서드
    public ContentResult getContent(Long contentId) {
        Content content =
                contentRepository
                        .findById(contentId)
                        .orElseThrow(() -> new CustomException(ContentErrorCode.CONTENT_NOT_FOUND));
        return ContentResult.from(content);
    }
}
