package com.tracek.global.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tracek.domain.auth.application.service.OAuthService;
import com.tracek.domain.fan.application.service.FanQueryService;
import com.tracek.domain.ranking.application.service.VisitRankingQueryService;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TestControllerTest {

    @Mock OAuthService oAuthService;
    @Mock VisitRankingQueryService visitRankingQueryService;
    @Mock FanQueryService fanQueryService;

    @Test
    void coversAuthenticationAndFanEndpoints() {
        var controller = new TestController(oAuthService, visitRankingQueryService, fanQueryService);
        var principal = new AuthenticationPrincipal(7L, "user", "ROLE_USER");
        when(oAuthService.getUserAndAccessToken(7L)).thenReturn("token");

        assertThat(controller.generateDevToken(7L)).isNotNull();
        assertThat(controller.getTestAuthWithSuccess()).isEqualTo("인증 성공");
        assertThat(controller.getArtistFanView(principal, 1L)).isNull();
        assertThat(controller.getArtistFanView(null, 1L)).isNull();
        assertThat(controller.getContentFanView(principal, 2L)).isNull();
        assertThat(controller.getContentFanView(null, 2L)).isNull();
        assertThat(controller.countArtistFansByUserId(7L)).isZero();
        assertThat(controller.countContentFansByUserId(7L)).isZero();

        verify(fanQueryService).getArtistFanView(7L, 1L);
        verify(fanQueryService).getArtistFanView(null, 1L);
        verify(fanQueryService).getContentFanView(7L, 2L);
        verify(fanQueryService).getContentFanView(null, 2L);
    }

    @Test
    void coversAllRankingEndpoints() {
        var controller = new TestController(oAuthService, visitRankingQueryService, fanQueryService);

        assertThat(controller.getArtistsByContent(1L, null, null, 10)).isNull();
        assertThat(controller.getLocationsByContent(1L, 5L, 2L, 10)).isNull();
        assertThat(controller.getMultiRankingByContent(1L)).isNull();

        assertThat(controller.getLocationsByArtist(2L, null, null, 10)).isNull();
        assertThat(controller.getContentsByArtist(2L, 5L, 3L, 10)).isNull();
        assertThat(controller.getMultiRankingByArtist(2L)).isNull();

        assertThat(controller.getArtistsByLocation(3L, null, null, 10)).isNull();
        assertThat(controller.getContentsByLocation(3L, 5L, 4L, 10)).isNull();
        assertThat(controller.getMultiRankingByLocation(3L)).isNull();

        verify(visitRankingQueryService).getArtistsByContent(org.mockito.ArgumentMatchers.eq(1L), any());
        verify(visitRankingQueryService).getLocationsByContent(org.mockito.ArgumentMatchers.eq(1L), any());
        verify(visitRankingQueryService).getMultiRankingByContent(1L);
        verify(visitRankingQueryService).getLocationsByArtist(org.mockito.ArgumentMatchers.eq(2L), any());
        verify(visitRankingQueryService).getContentsByArtist(org.mockito.ArgumentMatchers.eq(2L), any());
        verify(visitRankingQueryService).getMultiRankingByArtist(2L);
        verify(visitRankingQueryService).getArtistsByLocation(org.mockito.ArgumentMatchers.eq(3L), any());
        verify(visitRankingQueryService).getContentsByLocation(org.mockito.ArgumentMatchers.eq(3L), any());
        verify(visitRankingQueryService).getMultiRankingByLocation(3L);
    }
}
