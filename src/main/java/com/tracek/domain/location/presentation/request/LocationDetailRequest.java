package com.tracek.domain.location.presentation.request;

import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationDetailRequest {
    private Long lastCount;
    private Long lastId;
    private int size;

    public RankingCondition toCondition() {
        return new RankingCondition(this.lastCount, this.lastId, this.size);
    }
}
