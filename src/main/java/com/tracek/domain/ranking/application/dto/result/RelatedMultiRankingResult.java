package com.tracek.domain.ranking.application.dto.result;

import com.tracek.domain.ranking.domain.model.TargetId;
import lombok.Builder;

@Builder
public record RelatedMultiRankingResult(
        Long locationId, Long contentId, Long artistId, long totalVerificationCount) {

    public static RelatedMultiRankingResult from(TargetId targetId, long totalVerificationCount) {
        return RelatedMultiRankingResult.builder()
                .artistId(targetId.artistId())
                .contentId(targetId.contentId())
                .locationId(targetId.contentId())
                .build();
    }
}
