package com.tracek.domain.location.application;

import static com.tracek.domain.location.domain.model.QLocation.location;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.location.application.dto.LocationBoundsQuery;
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
            LocationSearchQuery query, int fetchSize) {
        return queryFactory
                .select(
                        Projections.constructor(
                                LocationSearchResult.LocationInfo.class,
                                location.id,
                                location.name,
                                location.category.stringValue(),
                                location.address.address,
                                location.mainImageUrl.imageUrl,
                                location.geoLocation.latitude,
                                location.geoLocation.longitude))
                .from(location)
                .where(
                        matchKeyword(query.getKeyword()),
                        ltLastLocationId(query.getLastLocationId()))
                .orderBy(location.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    // 통합검색용 - 이름만 매칭 (city/district 미포함), hasNext 판단을 위한 N+1 조회
    public List<LocationSearchResult.LocationInfo> searchLocationsByName(
            LocationSearchQuery query, int fetchSize) {
        return queryFactory
                .select(
                        Projections.constructor(
                                LocationSearchResult.LocationInfo.class,
                                location.id,
                                location.name,
                                location.category.stringValue(),
                                location.address.address,
                                location.mainImageUrl.imageUrl,
                                location.geoLocation.latitude,
                                location.geoLocation.longitude))
                .from(location)
                .where(
                        matchKeywordNameOnly(query.getKeyword()),
                        ltLastLocationId(query.getLastLocationId()))
                .orderBy(location.id.desc())
                .limit(fetchSize)
                .fetch();
    }

    private BooleanExpression matchKeywordNameOnly(String keyword) {
        String booleanKeyword = sanitizeBooleanKeyword(keyword);
        if (!StringUtils.hasText(booleanKeyword)) {
            return null;
        }

        booleanKeyword =
                Arrays.stream(booleanKeyword.split("\\s+"))
                        .map(w -> "+" + w)
                        .collect(Collectors.joining(" "));

        // Hibernate에 등록한 match_against1 함수 호출 -> name 단독 FULLTEXT(ngram) 인덱스를 탐
        return Expressions.booleanTemplate(
                "match_against1({0}, {1})", location.name, booleanKeyword);
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

    private BooleanExpression ltLastLocationId(Long lastLocationId) {
        return lastLocationId != null ? location.id.lt(lastLocationId) : null;
    }

    // 지도 bounds 범위 조회 - 페이징 없이 안전장치용 limit만 적용
    private static final int BOUNDS_RESULT_LIMIT = 500;

    public List<LocationSearchResult.LocationInfo> findLocationsWithinBounds(
            LocationBoundsQuery query) {
        return queryFactory
                .select(
                        Projections.constructor(
                                LocationSearchResult.LocationInfo.class,
                                location.id,
                                location.name,
                                location.category.stringValue(),
                                location.address.address,
                                location.mainImageUrl.imageUrl,
                                location.geoLocation.latitude,
                                location.geoLocation.longitude))
                .from(location)
                .where(
                        location.geoLocation.latitude.between(query.getSwLat(), query.getNeLat()),
                        location.geoLocation.longitude.between(query.getSwLng(), query.getNeLng()),
                        eqCategory(query.getCategory()))
                .limit(BOUNDS_RESULT_LIMIT)
                .fetch();
    }

    private BooleanExpression eqCategory(LocationCategory category) {
        return category != null ? location.category.eq(category) : null;
    }
}
