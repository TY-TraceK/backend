package com.tracek.domain.ranking.infrastructure.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.ranking.domain.model.QContentLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentLocationVisitRankingQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    private final QContentLocationVisitRanking contentLocationVisitRanking =
            QContentLocationVisitRanking.contentLocationVisitRanking;

    public List<RankingItem> findLocationsByContent(
            Long contentId, RankingSearchCriteria criteria) {
        return queryFactory
                .select(
                        Projections.constructor(
                                RankingItem.class,
                                contentLocationVisitRanking.locationId,
                                contentLocationVisitRanking.totalVerificationCount))
                .from(contentLocationVisitRanking)
                .where(
                        contentLocationVisitRanking.contentId.eq(contentId),
                        locationCondition(criteria))
                .orderBy(
                        contentLocationVisitRanking.totalVerificationCount.desc(),
                        contentLocationVisitRanking.locationId.asc())
                .limit(criteria.limit())
                .fetch();
    }

    public List<RankingItem> findContentsByLocation(
            Long locationId, RankingSearchCriteria criteria) {
        return queryFactory
                .select(
                        Projections.constructor(
                                RankingItem.class,
                                contentLocationVisitRanking.contentId,
                                contentLocationVisitRanking.totalVerificationCount))
                .from(contentLocationVisitRanking)
                .where(
                        contentLocationVisitRanking.locationId.eq(locationId),
                        contentCondition(criteria))
                .orderBy(
                        contentLocationVisitRanking.totalVerificationCount.desc(),
                        contentLocationVisitRanking.contentId.asc())
                .limit(criteria.limit())
                .fetch();
    }

    private BooleanBuilder locationCondition(RankingSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.lastCount() == null || criteria.lastId() == null) {
            return builder;
        }

        return builder.and(
                contentLocationVisitRanking
                        .totalVerificationCount
                        .lt(criteria.lastCount())
                        .or(
                                contentLocationVisitRanking
                                        .totalVerificationCount
                                        .eq(criteria.lastCount())
                                        .and(
                                                contentLocationVisitRanking.locationId.gt(
                                                        criteria.lastId()))));
    }

    private BooleanBuilder contentCondition(RankingSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.lastCount() == null || criteria.lastId() == null) {
            return builder;
        }

        return builder.and(
                contentLocationVisitRanking
                        .totalVerificationCount
                        .lt(criteria.lastCount())
                        .or(
                                contentLocationVisitRanking
                                        .totalVerificationCount
                                        .eq(criteria.lastCount())
                                        .and(
                                                contentLocationVisitRanking.contentId.gt(
                                                        criteria.lastId()))));
    }
}
