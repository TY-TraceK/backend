package com.tracek.domain.fan.presentaion.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tracek.domain.fan.application.dto.result.FanTargetResult;
import com.tracek.domain.fan.application.service.FanCommandService;
import com.tracek.domain.fan.application.service.FanQueryService;
import com.tracek.domain.fan.presentaion.dto.response.MyFanResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class FanControllerTest {

    private FanCommandService fanCommandService;
    private FanQueryService fanQueryService;
    private AuthenticationPrincipal principal;

    private FanController fanController;

    private Long artistId;
    private Long userId;
    private Long contentId;

    @BeforeEach
    void setUp() {
        fanCommandService = Mockito.mock(FanCommandService.class);
        fanQueryService = Mockito.mock(FanQueryService.class);

        userId = 1L;
        artistId = 10L;
        contentId = 20L;

        principal = new AuthenticationPrincipal(userId, "eee", "USER");

        fanController = new FanController(fanCommandService, fanQueryService);
    }

    @Test
    @DisplayName("아티스트 팬 등록 요청을 서비스에 위임한다.")
    void createArtistFan_success() {
        // when
        fanController.createArtistFan(principal, artistId);

        // then
        verify(fanCommandService).createArtistFan(userId, artistId);
    }

    @Test
    @DisplayName("아티스트 팬 등록 취소 요청을 서비스에 위임한다.")
    void deleteArtistFan_success() {
        // when
        fanController.deleteArtistFan(principal, artistId);

        // then
        verify(fanCommandService).deleteArtistFan(userId, artistId);
    }

    @Test
    @DisplayName("콘텐츠 팬 등록 요청을 서비스에 위임한다.")
    void createContentFan_success() {

        // when
        fanController.createContentFan(principal, contentId);

        // then
        verify(fanCommandService).createContentFan(userId, contentId);
    }

    @Test
    @DisplayName("콘텐츠 팬 등록 취소 요청을 서비스에 위임한다.")
    void deleteContentFan_success() {
        // when
        fanController.deleteContentFan(principal, contentId);

        // then
        verify(fanCommandService).deleteContentFan(userId, contentId);
    }

    @Test
    @DisplayName("내 팬 콘텐츠와 아티스트 목록을 조회한다.")
    void getMyFanTargets_success() {

        FanTargetResult content =
                FanTargetResult.builder()
                        .id(10L)
                        .name("런닝맨")
                        .description("콘텐츠")
                        .pictureUrl("content.jpg")
                        .fanCount(100L)
                        .totalVerificationCount(50L)
                        .build();

        FanTargetResult artist =
                FanTargetResult.builder()
                        .id(20L)
                        .name("유재석")
                        .description("아티스트")
                        .pictureUrl("artist.jpg")
                        .fanCount(200L)
                        .totalVerificationCount(80L)
                        .build();

        List<List<FanTargetResult>> results = List.of(List.of(content), List.of(artist));

        when(fanQueryService.getMyFanTargets(userId)).thenReturn(results);

        // when
        ApiResponse<MyFanResponse> response = fanController.getMyFanTargets(principal);

        // then
        assertThat(response).isNotNull();

        verify(fanQueryService).getMyFanTargets(userId);
    }
}
