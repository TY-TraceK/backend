package com.tracek.domain.user.application.dto.result;

import com.tracek.domain.user.domain.model.UserActivityProjection;
import lombok.Builder;

@Builder
public record UserActivityProjectionResult(
        Long fanCount, Long visitVerificationCount, Long likedCount, Long bookMarkCount) {

    public static UserActivityProjectionResult from(UserActivityProjection projection) {
        return UserActivityProjectionResult.builder()
                .fanCount(projection.artistFanCount() + projection.contentFanCount())
                .visitVerificationCount(projection.visitVerificationCount())
                .likedCount(projection.likedCount())
                .bookMarkCount(projection.bookMarkCount())
                .build();
    }
}
