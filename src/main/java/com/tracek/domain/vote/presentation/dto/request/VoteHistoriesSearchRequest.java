package com.tracek.domain.vote.presentation.dto.request;

import com.tracek.domain.vote.application.dto.condition.VoteHistoriesSearchCondition;
import com.tracek.domain.vote.domain.enums.VoteStatus;
import jakarta.validation.constraints.AssertTrue;
import java.time.LocalDate;

public record VoteHistoriesSearchRequest(
        Long artistId,
        Long contentId,
        Long locationId,
        VoteStatus voteStatus,
        LocalDate startDate,
        LocalDate endDate) {

    public VoteHistoriesSearchCondition toCondition(Long userId) {
        return VoteHistoriesSearchCondition.builder()
                .artistId(artistId)
                .contentId(contentId)
                .locationId(locationId)
                .voteStatus(voteStatus)
                .startDate(startDate != null ? startDate.atStartOfDay() : null)
                .endDate(endDate != null ? endDate.plusDays(1).atStartOfDay() : null)
                .userId(userId)
                .build();
    }

    @AssertTrue(message = "시작 날짜는 종료 날짜보다 늦을 수 없습니다.")
    private boolean isValidRange() {
        if (startDate == null || endDate == null) {
            return true;
        }
        return !startDate.isAfter(endDate);
    }
}
