package com.tracek.domain.visitVerification.infrastructure.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.model.QVisitVerification;
import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationHistoryCriteria;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VisitVerificationQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    private final QVisitVerification visitVerification = QVisitVerification.visitVerification;

    public Page<VisitVerification> findHistoriesByCriteria(
            VisitVerificationHistoryCriteria criteria, Pageable pageable) {

        List<VisitVerification> content =
                queryFactory
                        .selectFrom(visitVerification)
                        .where(
                                userIdEq(criteria.userId()),
                                artistIdEq(criteria.artistId()),
                                contentIdEq(criteria.contentId()),
                                locationIdEq(criteria.locationId()),
                                visitVerificationStatusEq(criteria.status()),
                                dateBetween(criteria.startDate(), criteria.endDate()))
                        .orderBy(visitVerification.verifiedAt.desc(), visitVerification.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        Long total =
                queryFactory
                        .select(visitVerification.count())
                        .from(visitVerification)
                        .where(
                                userIdEq(criteria.userId()),
                                artistIdEq(criteria.artistId()),
                                contentIdEq(criteria.contentId()),
                                locationIdEq(criteria.locationId()),
                                visitVerificationStatusEq(criteria.status()),
                                dateBetween(criteria.startDate(), criteria.endDate()))
                        .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? visitVerification.owner.eq(userId) : null;
    }

    private BooleanExpression artistIdEq(Long artistId) {
        return artistId != null ? visitVerification.verificationTarget.artistId.eq(artistId) : null;
    }

    private BooleanExpression contentIdEq(Long contentId) {
        return contentId != null
                ? visitVerification.verificationTarget.contentId.eq(contentId)
                : null;
    }

    private BooleanExpression locationIdEq(Long locationId) {
        return locationId != null
                ? visitVerification.verificationTarget.locationId.eq(locationId)
                : null;
    }

    private BooleanExpression visitVerificationStatusEq(VisitVerificationStatus status) {
        return status != null ? visitVerification.status.eq(status) : null;
    }

    private BooleanExpression dateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }

        if (startDate != null && endDate == null) {
            return visitVerification.verifiedAt.goe(startDate);
        }

        if (startDate == null) {
            return visitVerification.verifiedAt.lt(endDate);
        }

        return visitVerification
                .verifiedAt
                .goe(startDate)
                .and(visitVerification.verifiedAt.lt(endDate));
    }
}
