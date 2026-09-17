package com.tracek.domain.user.infrastructure.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.fan.domain.model.QArtistFan;
import com.tracek.domain.fan.domain.model.QContentFan;
import com.tracek.domain.location.domain.model.QLocationArchive;
import com.tracek.domain.location.domain.model.QLocationLike;
import com.tracek.domain.user.domain.model.QUser;
import com.tracek.domain.user.domain.model.UserActivityProjection;
import com.tracek.domain.visitVerification.domain.model.QVisitVerification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserQueryDslRepository {

    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    private final QVisitVerification visitVerification = QVisitVerification.visitVerification;

    private final QLocationLike locationLike = QLocationLike.locationLike;

    private final QArtistFan artistFan = QArtistFan.artistFan;
    private final QContentFan contentFan = QContentFan.contentFan;

    private final QLocationArchive locationArchive = QLocationArchive.locationArchive;

    public UserActivityProjection findUserActivity(Long userId) {

        return queryFactory
                .select(
                        Projections.constructor(
                                UserActivityProjection.class,
                                JPAExpressions.select(artistFan.count())
                                        .from(artistFan)
                                        .where(artistFan.userId.eq(userId)),
                                JPAExpressions.select(contentFan.count())
                                        .from(contentFan)
                                        .where(contentFan.userId.eq(userId)),
                                JPAExpressions.select(visitVerification.count())
                                        .from(visitVerification)
                                        .where(visitVerification.owner.eq(userId)),
                                JPAExpressions.select(locationLike.count())
                                        .from(locationLike)
                                        .where(locationLike.userId.eq(userId)),
                                JPAExpressions.select(locationArchive.count())
                                        .from(locationArchive)
                                        .where(locationArchive.userId.eq(userId))))
                .from(user)
                .where(user.id.eq(userId))
                .fetchOne();
    }
}
