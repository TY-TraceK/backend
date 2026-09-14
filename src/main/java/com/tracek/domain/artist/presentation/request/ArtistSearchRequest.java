package com.tracek.domain.artist.presentation.request;

import com.tracek.domain.artist.application.dto.ArtistSearchQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArtistSearchRequest {
    @Schema(description = "검색 키워드(아티스트 이름·alias 대상 전문 검색). 없으면 빈 목록을 반환합니다.", example = "아이유")
    private String keyword;

    @Schema(description = "커서 - 이전 페이지 응답의 lastId. 미입력 시 첫 페이지부터 조회합니다.", example = "42")
    private Long lastArtistId;

    @Schema(description = "페이지 크기, 1 미만이면 기본값 20 적용", example = "20")
    private Integer size;

    public ArtistSearchQuery toQuery() {
        int defaultSize = (this.size == null || this.size < 1) ? 20 : this.size;
        return ArtistSearchQuery.of(this.keyword, this.lastArtistId, defaultSize);
    }
}
