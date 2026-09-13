package com.tracek.domain.ranking.infrastructure.persistence.qsdl;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.ranking.domain.model.QContentArtistLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import com.tracek.domain.ranking.domain.model.TargetId;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentArtistLocationVisitRankingQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    private final QContentArtistLocationVisitRanking contentArtistLocationVisitRanking =
            QContentArtistLocationVisitRanking.contentArtistLocationVisitRanking;

    public List<RankingItem> findRankingsByArtist(Long artistId, RankingSearchCriteria criteria) {
        JPAQuery<RankingItem> query =
                queryFactory
                        .select(
                                Projections.constructor(
                                        RankingItem.class,
                                        Projections.constructor(
                                                TargetId.class,
                                                contentArtistLocationVisitRanking.locationId,
                                                contentArtistLocationVisitRanking.contentId,
                                                contentArtistLocationVisitRanking.artistId),
                                        contentArtistLocationVisitRanking.totalVerificationCount))
                        .from(contentArtistLocationVisitRanking)
                        .where(
                                contentArtistLocationVisitRanking.artistId.eq(artistId),
                                artistCondition(criteria))
                        .orderBy(
                                contentArtistLocationVisitRanking.totalVerificationCount.desc(),
                                contentArtistLocationVisitRanking.id.desc());

        if (criteria != null && criteria.limit() != null) {
            query.limit(criteria.limit());
        }

        return query.fetch();
    }

    public List<RankingItem> findRankingsByContent(Long contentId, RankingSearchCriteria criteria) {
        JPAQuery<RankingItem> query =
                queryFactory
                        .select(
                                Projections.constructor(
                                        RankingItem.class,
                                        Projections.constructor(
                                                TargetId.class,
                                                contentArtistLocationVisitRanking.locationId,
                                                contentArtistLocationVisitRanking.contentId,
                                                contentArtistLocationVisitRanking.artistId),
                                        contentArtistLocationVisitRanking.totalVerificationCount))
                        .from(contentArtistLocationVisitRanking)
                        .where(
                                contentArtistLocationVisitRanking.contentId.eq(contentId),
                                contentCondition(criteria))
                        .orderBy(
                                contentArtistLocationVisitRanking.totalVerificationCount.desc(),
                                contentArtistLocationVisitRanking.id.desc());

        if (criteria != null && criteria.limit() != null) {
            query.limit(criteria.limit());
        }

        return query.fetch();
    }

    public List<RankingItem> findRankingsByLocation(
            Long locationId, RankingSearchCriteria criteria) {
        JPAQuery<RankingItem> query =
                queryFactory
                        .select(
                                Projections.constructor(
                                        RankingItem.class,
                                        Projections.constructor(
                                                TargetId.class,
                                                contentArtistLocationVisitRanking.locationId,
                                                contentArtistLocationVisitRanking.contentId,
                                                contentArtistLocationVisitRanking.artistId),
                                        contentArtistLocationVisitRanking.totalVerificationCount))
                        .from(contentArtistLocationVisitRanking)
                        .where(
                                contentArtistLocationVisitRanking.locationId.eq(locationId),
                                locationCondition(criteria))
                        .orderBy(
                                contentArtistLocationVisitRanking.totalVerificationCount.desc(),
                                contentArtistLocationVisitRanking.id.desc());

        if (criteria != null && criteria.limit() != null) {
            query.limit(criteria.limit());
        }

        return query.fetch();
    }

    private BooleanBuilder contentCondition(RankingSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria == null || criteria.lastCount() == null || criteria.lastId() == null) {
            return builder;
        }

        return builder.and(
                contentArtistLocationVisitRanking
                        .totalVerificationCount
                        .lt(criteria.lastCount())
                        .or(
                                contentArtistLocationVisitRanking
                                        .totalVerificationCount
                                        .eq(criteria.lastCount())
                                        .and(
                                                contentArtistLocationVisitRanking.contentId.gt(
                                                        criteria.lastId()))));
    }

    private BooleanBuilder locationCondition(RankingSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria == null || criteria.lastCount() == null || criteria.lastId() == null) {
            return builder;
        }

        return builder.and(
                contentArtistLocationVisitRanking
                        .totalVerificationCount
                        .lt(criteria.lastCount())
                        .or(
                                contentArtistLocationVisitRanking
                                        .totalVerificationCount
                                        .eq(criteria.lastCount())
                                        .and(
                                                contentArtistLocationVisitRanking.locationId.gt(
                                                        criteria.lastId()))));
    }

    private BooleanBuilder artistCondition(RankingSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria == null || criteria.lastCount() == null || criteria.lastId() == null) {
            return builder;
        }

        return builder.and(
                contentArtistLocationVisitRanking
                        .totalVerificationCount
                        .lt(criteria.lastCount())
                        .or(
                                contentArtistLocationVisitRanking
                                        .totalVerificationCount
                                        .eq(criteria.lastCount())
                                        .and(
                                                contentArtistLocationVisitRanking.artistId.gt(
                                                        criteria.lastId()))));
    }
}
