package com.tracek.domain.vote.infrastructure.persistence;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.tracek.domain.vote.domain.enums.VoteStatus;
import com.tracek.domain.vote.domain.model.QVote;
import com.tracek.domain.vote.domain.model.Vote;
import com.tracek.domain.vote.domain.model.VoteHistoryCriteria;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VoteQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    private final QVote vote = QVote.vote;

    public Page<Vote> findHistoriesByCriteria(VoteHistoryCriteria criteria, Pageable pageable) {

        List<Vote> content =
                queryFactory
                        .selectFrom(vote)
                        .where(
                                userIdEq(criteria.userId()),
                                artistIdEq(criteria.artistId()),
                                contentIdEq(criteria.contentId()),
                                locationIdEq(criteria.locationId()),
                                voteStatusEq(criteria.voteStatus()),
                                dateBetween(criteria.startDate(), criteria.endDate()))
                        .orderBy(vote.votedAt.desc(), vote.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetch();

        Long total =
                queryFactory
                        .select(vote.count())
                        .from(vote)
                        .where(
                                userIdEq(criteria.userId()),
                                artistIdEq(criteria.artistId()),
                                contentIdEq(criteria.contentId()),
                                locationIdEq(criteria.locationId()),
                                voteStatusEq(criteria.voteStatus()),
                                dateBetween(criteria.startDate(), criteria.endDate()))
                        .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

    private BooleanExpression userIdEq(Long userId) {
        return userId != null ? vote.voteOwner.eq(userId) : null;
    }

    private BooleanExpression artistIdEq(Long artistId) {
        return artistId != null ? vote.voteTarget.artistId.eq(artistId) : null;
    }

    private BooleanExpression contentIdEq(Long contentId) {
        return contentId != null ? vote.voteTarget.contentId.eq(contentId) : null;
    }

    private BooleanExpression locationIdEq(Long locationId) {
        return locationId != null ? vote.voteTarget.locationId.eq(locationId) : null;
    }

    private BooleanExpression voteStatusEq(VoteStatus status) {
        return status != null ? vote.voteStatus.eq(status) : null;
    }

    private BooleanExpression dateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null && endDate == null) {
            return null;
        }

        if (startDate != null && endDate == null) {
            return vote.votedAt.goe(startDate);
        }

        if (startDate == null) {
            return vote.votedAt.lt(endDate);
        }

        return vote.votedAt.goe(startDate).and(vote.votedAt.lt(endDate));
    }
}
