package com.tracek.domain.content.application.service;

import com.tracek.domain.content.application.ContentQueryRepository;
import com.tracek.domain.content.application.dto.ContentSearchQuery;
import com.tracek.domain.content.application.dto.ContentSearchResult;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentSearchQueryService {
    private final ContentQueryRepository contentQueryRepository;

    public ContentSearchResult searchContents(ContentSearchQuery query) {

        if (!StringUtils.hasText(query.getKeyword())) {
            return ContentSearchResult.of(Collections.emptyList(), 0);
        }

        // hasNext 판별을 위한 N+1 조회
        int fetchSize = query.getSize() + 1;
        List<ContentSearchResult.ContentInfo> contents =
                contentQueryRepository.searchContents(query, fetchSize);

        return ContentSearchResult.of(contents, query.getSize());
    }
}
