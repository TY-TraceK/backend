package com.tracek.domain.fan.presentaion.controller;

import com.tracek.domain.fan.application.service.FanCommandService;
import com.tracek.domain.fan.presentaion.controller.docs.FanControllerDocs;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FanController implements FanControllerDocs {

    private final FanCommandService fanCommandService;

    @Override
    @PostMapping("/artists/{artistId}/fans")
    public ApiResponse<Void> createArtistFan(AuthenticationPrincipal principal, Long artistId) {
        fanCommandService.createArtistFan(principal.userId(), artistId);
        return ApiResponse.success(GeneralSuccessCode.CREATED);
    }

    @DeleteMapping("/artists/{artistId}/fans")
    @Override
    public ApiResponse<Void> deleteArtistFan(AuthenticationPrincipal principal, Long artistId) {
        fanCommandService.deleteArtistFan(principal.userId(), artistId);
        return ApiResponse.success(GeneralSuccessCode.OK);
    }

    @Override
    @PostMapping("/contents/{contentId}/fans")
    public ApiResponse<Void> createContentFan(AuthenticationPrincipal principal, Long contentId) {
        fanCommandService.createContentFan(principal.userId(), contentId);
        return ApiResponse.success(GeneralSuccessCode.CREATED);
    }

    @DeleteMapping("/contents/{contentId}/fans")
    @Override
    public ApiResponse<Void> deleteContentFan(AuthenticationPrincipal principal, Long contentId) {
        fanCommandService.deleteContentFan(principal.userId(), contentId);
        return ApiResponse.success(GeneralSuccessCode.OK);
    }
}
