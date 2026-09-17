package com.tracek.domain.visitVerification.infrastructure.persistence;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.artist.domain.model.QArtist;
import com.tracek.domain.content.domain.model.QContent;
import com.tracek.domain.location.domain.model.QLocation;
import com.tracek.domain.location.domain.model.QLocationArchive;
import com.tracek.domain.location.domain.model.QLocationLike;
import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.model.QVisitVerification;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationHistoryCriteria;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationView;
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

    private final QContent content = QContent.content;

    private final QArtist artist = QArtist.artist;

    private final QLocationLike locationLike = QLocationLike.locationLike;

    private final QLocationArchive locationArchive = QLocationArchive.locationArchive;

    public List<VisitVerificationView> findHistoriesByCriteria(
            VisitVerificationHistoryCriteria criteria) {

        int pageSize = criteria.size() != null ? criteria.size() : 20;

        List<LocalDate> targetDates = findTargetDates(criteria, pageSize);

        if (targetDates.isEmpty()) {
            return List.of();
        }

        NumberPath<Long> artistId = Expressions.numberPath(Long.class, "artistId");

        return queryFactory
                .from(visitVerification)
                .leftJoin(location)
                .on(visitVerification.locationId.eq(location.id))
                .leftJoin(content)
                .on(visitVerification.verificationTarget.contentId.eq(content.id))
                .leftJoin(visitVerification.verificationTarget.artistIds, artistId)
                .leftJoin(artist)
                .on(artist.id.eq(artistId))
                .leftJoin(locationLike)
                .on(
                        locationLike
                                .locationId
                                .eq(location.id)
                                .and(locationLike.userId.eq(criteria.userId())))
                .leftJoin(locationArchive)
                .on(
                        locationArchive
                                .locationId
                                .eq(location.id)
                                .and(locationArchive.userId.eq(criteria.userId())))
                .where(
                        userIdEq(criteria.userId()),
                        artistIdEq(criteria.artistId()),
                        contentIdEq(criteria.contentId()),
                        locationIdEq(criteria.locationId()),
                        cityEq(criteria.city()),
                        visitVerificationStatusEq(criteria.status()),
                        dateBetween(criteria.startDate(), criteria.endDate()),
                        visitVerification.validVerifiedAt.in(targetDates))
                .orderBy(
                        visitVerification.validVerifiedAt.desc(),
                        visitVerification.verifiedAt.asc(),
                        visitVerification.id.desc())
                .transform(
                        groupBy(visitVerification.id)
                                .list(
                                        Projections.constructor(
                                                VisitVerificationView.class,
                                                visitVerification.id,
                                                location.id,
                                                location.name,
                                                location.address.address,
                                                location.mainImageUrl.imageUrl,
                                                location.address.city,
                                                content.id,
                                                content.title,
                                                list(
                                                        Projections.constructor(
                                                                VisitVerificationView.ArtistView
                                                                        .class,
                                                                artist.id,
                                                                artist.name)),
                                                visitVerification.validVerifiedAt,
                                                visitVerification.verifiedAt,
                                                visitVerification.status,
                                                locationLike.id.isNotNull(),
                                                locationArchive.id.isNotNull())));
    }

    private List<LocalDate> findTargetDates(
            VisitVerificationHistoryCriteria criteria, int pageSize) {

        return queryFactory
                .select(visitVerification.validVerifiedAt)
                .distinct()
                .from(visitVerification)
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
                .orderBy(visitVerification.validVerifiedAt.desc())
                .limit(pageSize + 1)
                .fetch();
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? visitVerification.owner.eq(userId) : null;
    }

    private BooleanExpression artistIdEq(Long artistId) {
        return artistId != null
                ? visitVerification.verificationTarget.artistIds.contains(artistId)
                : null;
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
