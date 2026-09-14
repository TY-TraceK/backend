package com.tracek.domain.content.application;

import static com.tracek.domain.content.domain.model.QContent.content;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.content.application.dto.ContentSearchQuery;
import com.tracek.domain.content.application.dto.ContentSearchResult;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ContentQueryRepository {
    private final JPAQueryFactory queryFactory;

    // hasNext 판단을 위한 N+1 조회
    public List<ContentSearchResult.ContentInfo> searchContents(
            ContentSearchQuery query, int fetchSize) {
        return queryFactory
                .select(
                        Projections.constructor(
                                ContentSearchResult.ContentInfo.class,
                                content.id,
                                content.title,
                                content.category.stringValue(),
                                content.pictureUrl.imageUrl))
                .from(content)
                .where(matchKeyword(query.getKeyword()), ltLastContentId(query.getLastContentId()))
                .orderBy(content.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    private BooleanExpression matchKeyword(String keyword) {
        String booleanKeyword = sanitizeBooleanKeyword(keyword);
        if (!StringUtils.hasText(booleanKeyword)) {
            return null;
        }

        booleanKeyword =
                Arrays.stream(booleanKeyword.split("\\s+"))
                        .map(w -> "+" + w)
                        .collect(Collectors.joining(" "));

        // Hibernate에 등록한 match_against1 함수 호출 -> title 단독 FULLTEXT(ngram) 인덱스를 탐
        return Expressions.booleanTemplate(
                "match_against1({0}, {1})", content.title, booleanKeyword);
    }

    // 키워드 정제
    private String sanitizeBooleanKeyword(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return "";
        }
        // MySQL Boolean Mode 연산자 특수문자 제거
        String sanitized = keyword.replaceAll("[+\\-*\"()<>&~@]", " ").trim();

        // 특수문자만 입력해서 정제 후 빈 문자열이 된 경우 예외 처리
        if (sanitized.isEmpty()) {
            return "";
        }

        return sanitized;
    }

    private BooleanExpression ltLastContentId(Long lastContentId) {
        return lastContentId != null ? content.id.lt(lastContentId) : null;
    }
}
