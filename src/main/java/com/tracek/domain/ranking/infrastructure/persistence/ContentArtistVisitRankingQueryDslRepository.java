package com.tracek.domain.ranking.infrastructure.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.ranking.domain.model.QContentArtistVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ContentArtistVisitRankingQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    private final QContentArtistVisitRanking contentArtistVisitRanking =
            QContentArtistVisitRanking.contentArtistVisitRanking;

    public List<RankingItem> findArtistsByContent(Long contentId, RankingSearchCriteria criteria) {
        return queryFactory
                .select(
                        Projections.constructor(
                                RankingItem.class,
                                contentArtistVisitRanking.artistId,
                                contentArtistVisitRanking.totalVerificationCount))
                .from(contentArtistVisitRanking)
                .where(contentArtistVisitRanking.contentId.eq(contentId), artistCondition(criteria))
                .orderBy(
                        contentArtistVisitRanking.totalVerificationCount.desc(),
                        contentArtistVisitRanking.artistId.asc())
                .limit(criteria.limit())
                .fetch();
    }

    public List<RankingItem> findContentsByArtist(Long artistId, RankingSearchCriteria criteria) {
        return queryFactory
                .select(
                        Projections.constructor(
                                RankingItem.class,
                                contentArtistVisitRanking.contentId,
                                contentArtistVisitRanking.totalVerificationCount))
                .from(contentArtistVisitRanking)
                .where(contentArtistVisitRanking.artistId.eq(artistId), contentCondition(criteria))
                .orderBy(
                        contentArtistVisitRanking.totalVerificationCount.desc(),
                        contentArtistVisitRanking.contentId.asc())
                .limit(criteria.limit())
                .fetch();
    }

    private BooleanBuilder artistCondition(RankingSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.lastCount() == null || criteria.lastId() == null) {
            return builder;
        }

        return builder.and(
                contentArtistVisitRanking
                        .totalVerificationCount
                        .lt(criteria.lastCount())
                        .or(
                                contentArtistVisitRanking
                                        .totalVerificationCount
                                        .eq(criteria.lastCount())
                                        .and(
                                                contentArtistVisitRanking.artistId.gt(
                                                        criteria.lastId()))));
    }

    private BooleanBuilder contentCondition(RankingSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.lastCount() == null || criteria.lastId() == null) {
            return builder;
        }

        return builder.and(
                contentArtistVisitRanking
                        .totalVerificationCount
                        .lt(criteria.lastCount())
                        .or(
                                contentArtistVisitRanking
                                        .totalVerificationCount
                                        .eq(criteria.lastCount())
                                        .and(
                                                contentArtistVisitRanking.contentId.gt(
                                                        criteria.lastId()))));
    }
}
