package com.tracek.domain.user.domain.model;

import lombok.Builder;

@Builder
public record UserActivityProjection(
        Long artistFanCount,
        Long contentFanCount,
        Long visitVerificationCount,
        Long likedCount,
        Long bookMarkCount) {}
