package com.tracek.domain.ranking.presentaion.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.tracek.domain.ranking.application.dto.result.ContentCurationResult;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import com.tracek.domain.ranking.presentaion.dto.response.ContentCurationResponse;
import com.tracek.global.response.ApiResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RankingControllerTest {

    @Mock private VisitRankingQueryService visitRankingQueryService;

    private RankingController controller;

    @BeforeEach
    void setUp() {
        controller = new RankingController(visitRankingQueryService);
    }

    @Test
    @DisplayName("저방문 콘텐츠 큐레이션 결과를 성공 응답으로 반환한다")
    void getLowVisitContentCuration() {
        given(visitRankingQueryService.getLowVisitContentCuration())
                .willReturn(
                        new ContentCurationResult(
                                1L, "런닝맨", 10L, List.of("송도해수욕장", "송도해상케이블카", "흰여울문화마을")));

        ApiResponse<ContentCurationResponse> response = controller.getLowVisitContentCuration();

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData().contentId()).isEqualTo(1L);
        assertThat(response.getData().locationNames())
                .containsExactly("송도해수욕장", "송도해상케이블카", "흰여울문화마을");
    }

    @Test
    @DisplayName("큐레이션 대상이 없으면 data가 null인 성공 응답을 반환한다")
    void getLowVisitContentCurationEmpty() {
        given(visitRankingQueryService.getLowVisitContentCuration()).willReturn(null);

        ApiResponse<ContentCurationResponse> response = controller.getLowVisitContentCuration();

        assertThat(response.getIsSuccess()).isTrue();
        assertThat(response.getData()).isNull();
    }
}
