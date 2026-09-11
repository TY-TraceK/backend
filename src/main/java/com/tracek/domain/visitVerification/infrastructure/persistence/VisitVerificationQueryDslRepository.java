package com.tracek.domain.visitVerification.infrastructure.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.location.domain.model.QLocation;
import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.model.QVisitVerification;
import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationHistoryCriteria;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VisitVerificationQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    private final QVisitVerification visitVerification = QVisitVerification.visitVerification;
    private final QLocation location = QLocation.location;

    public List<VisitVerification> findHistoriesByCriteria(
            VisitVerificationHistoryCriteria criteria) {

        int pageSize = criteria.size() != null ? criteria.size() : 20;

        return queryFactory
                .selectFrom(visitVerification)
                .leftJoin(location)
                .on(visitVerification.locationId.eq(location.id))
                .where(
                        userIdEq(criteria.userId()),
                        artistIdEq(criteria.artistId()),
                        contentIdEq(criteria.contentId()),
                        locationIdEq(criteria.locationId()),
                        cityEq(criteria.city()),
                        visitVerificationStatusEq(criteria.status()),
                        dateBetween(criteria.startDate(), criteria.endDate()),
                        ltCursorDate(criteria.cursorDate()))
                .orderBy(visitVerification.verifiedAt.desc(), visitVerification.id.desc())
                .limit(pageSize + 1)
                .fetch();
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
        return locationId != null ? visitVerification.locationId.eq(locationId) : null;
    }

    private BooleanExpression cityEq(String city) {
        return city != null && !city.isBlank() ? location.address.city.eq(city) : null;
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

    private BooleanExpression ltCursorDate(LocalDate cursorDate) {
        return cursorDate != null ? visitVerification.validVerifiedAt.lt(cursorDate) : null;
    }
}
