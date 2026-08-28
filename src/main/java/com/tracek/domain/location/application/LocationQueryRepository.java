package com.tracek.domain.location.application;

import static com.tracek.domain.location.domain.model.QLocation.location;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.location.application.dto.LocationSearchQuery;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import com.tracek.domain.location.domain.model.LocationCategory;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class LocationQueryRepository {
    private final JPAQueryFactory queryFactory;

    // hasNext 판단을 위한 N+1 조회
    public List<LocationSearchResult.LocationInfo> searchLocations(
            LocationSearchQuery query, LocationCategory category, int fetchSize) {
        return queryFactory
                .select(
                        Projections.constructor(
                                LocationSearchResult.LocationInfo.class,
                                location.id,
                                location.name,
                                location.category.stringValue(),
                                location.address.address,
                                location.mainImageUrl.imageUrl))
                .from(location)
                .where(
                        matchKeyword(query.getKeyword()),
                        eqCategory(category),
                        ltLastLocationId(query.getLastLocationId()))
                .orderBy(location.id.desc())
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

        // Hibernate에 등록한 match_against 함수 호출 -> name/city/district FULLTEXT(ngram) 인덱스를 탐
        return Expressions.booleanTemplate(
                "match_against({0}, {1}, {2}, {3})",
                location.name, location.address.city, location.address.district, booleanKeyword);
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

    private BooleanExpression eqCategory(LocationCategory category) {
        return category != null ? location.category.eq(category) : null;
    }

    private BooleanExpression ltLastLocationId(Long lastLocationId) {
        return lastLocationId != null ? location.id.lt(lastLocationId) : null;
    }

    // 단순 Like에서 full-text index로 전환
    private BooleanExpression containKeyword(String keyword) {
        return StringUtils.hasText(keyword) ? location.name.contains(keyword) : null;
    }
}
