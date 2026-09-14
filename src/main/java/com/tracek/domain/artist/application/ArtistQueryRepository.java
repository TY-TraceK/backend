package com.tracek.domain.artist.application;

import static com.tracek.domain.artist.domain.model.QArtist.artist;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.artist.application.dto.ArtistSearchQuery;
import com.tracek.domain.artist.application.dto.ArtistSearchResult;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class ArtistQueryRepository {
    private final JPAQueryFactory queryFactory;

    public List<ArtistSearchResult.ArtistInfo> searchArtists(
            ArtistSearchQuery query, int fetchSize) {
        return queryFactory
                .select(
                        Projections.constructor(
                                ArtistSearchResult.ArtistInfo.class,
                                artist.id,
                                artist.name,
                                artist.alias,
                                artist.pictureUrl.imageUrl,
                                artist.group.id,
                                artist.isGroup))
                .from(artist)
                .where(matchKeyword(query.getKeyword()), ltLastArtistId(query.getLastArtistId()))
                .orderBy(artist.id.desc())
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

        // Hibernate에 등록한 match_against 함수 호출 -> name/alias FULLTEXT(ngram) 인덱스를 탐
        return Expressions.booleanTemplate(
                "match_against2({0}, {1}, {2})", artist.name, artist.alias, booleanKeyword);
    }

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

    private BooleanExpression ltLastArtistId(Long lastArtistId) {
        return lastArtistId != null ? artist.id.lt(lastArtistId) : null;
    }
}
