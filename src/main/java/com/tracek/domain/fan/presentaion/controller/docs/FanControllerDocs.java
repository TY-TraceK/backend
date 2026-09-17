package com.tracek.domain.fan.presentaion.controller.docs;

import com.tracek.domain.fan.presentaion.dto.response.MyFanResponse;
import com.tracek.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Tag(name = "FAN", description = "팬")
public interface FanControllerDocs {

    @Operation(summary = "아티스트 팬 생성")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "아티스트 팬 생성 성공",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            })
    @SecurityRequirement(name = "jwtAuth")
    ApiResponse<Void> createArtistFan(
            @Parameter(hidden = true) @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal,
            @PathVariable Long artistId);

    @SecurityRequirement(name = "jwtAuth")
    @DeleteMapping("/artists/{artistId}/fans")
    ApiResponse<Void> deleteArtistFan(
            @Parameter(hidden = true) @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal,
            @PathVariable Long artistId);

    @Operation(summary = "콘텐츠 팬 생성")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "콘텐츠 팬 생성 성공",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            })
    @SecurityRequirement(name = "jwtAuth")
    ApiResponse<Void> createContentFan(
            @Parameter(hidden = true) @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal,
            @PathVariable Long contentId);

    @SecurityRequirement(name = "jwtAuth")
    @DeleteMapping("/contents/{contentId}/fans")
    ApiResponse<Void> deleteContentFan(
            @Parameter(hidden = true) @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal,
            @PathVariable Long contentId);

    @SecurityRequirement(name = "jwtAuth")
    @GetMapping("/users/me/fans")
    ApiResponse<MyFanResponse> getMyFanTargets(
            @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal);
}
