package com.tracek.domain.vote.presentation.controller.docs;

import com.tracek.domain.vote.presentation.dto.VoteCancelResponse;
import com.tracek.domain.vote.presentation.dto.request.VoteCreateRequest;
import com.tracek.domain.vote.presentation.dto.response.VoteCreateResponse;
import com.tracek.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "VOTE", description = "투표 관련 API")
public interface VoteControllerDocs {

    @Operation(
            summary = "관광지 투표 생성",
            description =
                    "로그인한 사용자가 특정 관광지-콘텐츠-아티스트 조합에 투표를 진행합니다. 동일 장소에 유효한 투표가 이미 존재하면 중복 투표로 차단됩니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "투표 생성 성공",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description =
                                "1. 요청 파라미터 유효성 검증 실패 (필수 필드 누락/공백)\n2. 이미 해당 관광지에 유효한 투표를 완료한 경우 (ALREADY_VOTED)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "인증 토큰이 누락되었거나 유효하지 않은 경우 (UNAUTHORIZED)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description =
                                "요청한 관광지/아티스트/콘텐츠 매핑 정보를 찾을 수 없는 경우 (MAPPING_NOT_FOUND / VOTE_TARGET_NOT_FOUND)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "409",
                        description = "동시에 중복 투표 요청이 인입되어 DB 제약조건 충돌이 발생한 경우 (CONFLICT)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            })
    @PostMapping()
    @SecurityRequirement(name = "jwtAuth")
    ApiResponse<VoteCreateResponse> createVote(
            @Parameter(hidden = true) @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal,
            @Valid @RequestBody VoteCreateRequest request);

    @Operation(
            summary = "관광지 투표 취소",
            description = "로그인한 사용자가 해당 관광지의 투표를 취소합니다. 투표 취소는 오늘 건에 대해서만 가능합니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "투표 취소 성공 (이미 취소된 건일 경우 멱등하게 정상 응답 처리)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description = "당일 생성된 투표가 아니라서 취소할 수 없는 경우 (VOTE_CANNOT_BE_CANCELLED)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "인증 토큰이 누락되었거나 유효하지 않은 경우 (UNAUTHORIZED)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "403",
                        description = "투표 소유자가 아닌 유저가 취소를 요청한 경우 (UNAUTHORIZED_VOTE_ACCESS)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "존재하지 않는 투표 ID로 취소를 요청한 경우 (VOTE_NOT_FOUND)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            })
    @SecurityRequirement(name = "jwtAuth")
    ApiResponse<VoteCancelResponse> cancelVote(
            @Parameter(hidden = true) @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal,
            @Valid @PathVariable Long voteId);
}
