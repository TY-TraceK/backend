package com.tracek.domain.content.presentation.request;

import com.tracek.domain.ranking.application.dto.condition.RankingCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContentDetailRequest {
    private String city;
    private Long lastCount;
    private Long lastId;
    private int size;

    public RankingCondition toCondition() {
        return new RankingCondition(this.lastCount, this.lastId, this.size);
    }
}
