package com.tracek.domain.location.presentation.request;

import com.tracek.domain.location.application.dto.LocationSearchQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LocationSearchRequest {

    @Schema(description = "검색 키워드(관광지 이름·시/도·구군 대상 전문 검색). 없으면 빈 목록을 반환합니다.", example = "경복궁")
    private String keyword;

    @Schema(description = "카테고리로 결과를 추가 필터링합니다 (선택, 예: ATTRACTION, CAFE)", example = "ATTRACTION")
    private String category;

    @Schema(description = "커서 - 이전 페이지 응답의 lastId. 미입력 시 첫 페이지부터 조회합니다.", example = "42")
    private Long lastLocationId;

    @Schema(description = "페이지 크기, 1 미만이면 기본값 20 적용", example = "20")
    private Integer size;

    public LocationSearchQuery toQuery() {
        int defaultSize = (this.size == null || this.size < 1) ? 20 : this.size;
        return LocationSearchQuery.of(
                this.keyword, this.category, this.lastLocationId, defaultSize);
    }
}
