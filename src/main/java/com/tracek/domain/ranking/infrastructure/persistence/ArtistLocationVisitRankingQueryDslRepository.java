package com.tracek.domain.ranking.infrastructure.persistence;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.ranking.domain.model.QArtistLocationVisitRanking;
import com.tracek.domain.ranking.domain.model.RankingItem;
import com.tracek.domain.ranking.domain.model.RankingSearchCriteria;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ArtistLocationVisitRankingQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    private final QArtistLocationVisitRanking artistLocationVisitRanking =
            QArtistLocationVisitRanking.artistLocationVisitRanking;

    public List<RankingItem> findLocationsByArtist(Long artistId, RankingSearchCriteria criteria) {
        return queryFactory
                .select(
                        Projections.constructor(
                                RankingItem.class,
                                artistLocationVisitRanking.locationId,
                                artistLocationVisitRanking.totalVerificationCount))
                .from(artistLocationVisitRanking)
                .where(
                        artistLocationVisitRanking.artistId.eq(artistId),
                        locationCondition(criteria))
                .orderBy(
                        artistLocationVisitRanking.totalVerificationCount.desc(),
                        artistLocationVisitRanking.locationId.asc())
                .limit(criteria.limit())
                .fetch();
    }

    public List<RankingItem> findArtistsByLocation(
            Long locationId, RankingSearchCriteria criteria) {
        return queryFactory
                .select(
                        Projections.constructor(
                                RankingItem.class,
                                artistLocationVisitRanking.artistId,
                                artistLocationVisitRanking.totalVerificationCount))
                .from(artistLocationVisitRanking)
                .where(
                        artistLocationVisitRanking.locationId.eq(locationId),
                        artistCondition(criteria))
                .orderBy(
                        artistLocationVisitRanking.totalVerificationCount.desc(),
                        artistLocationVisitRanking.artistId.asc())
                .limit(criteria.limit())
                .fetch();
    }

    private BooleanBuilder locationCondition(RankingSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.lastCount() == null || criteria.lastId() == null) {
            return builder;
        }

        return builder.and(
                artistLocationVisitRanking
                        .totalVerificationCount
                        .lt(criteria.lastCount())
                        .or(
                                artistLocationVisitRanking
                                        .totalVerificationCount
                                        .eq(criteria.lastCount())
                                        .and(
                                                artistLocationVisitRanking.locationId.gt(
                                                        criteria.lastId()))));
    }

    private BooleanBuilder artistCondition(RankingSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.lastCount() == null || criteria.lastId() == null) {
            return builder;
        }

        return builder.and(
                artistLocationVisitRanking
                        .totalVerificationCount
                        .lt(criteria.lastCount())
                        .or(
                                artistLocationVisitRanking
                                        .totalVerificationCount
                                        .eq(criteria.lastCount())
                                        .and(
                                                artistLocationVisitRanking.artistId.gt(
                                                        criteria.lastId()))));
    }
}
