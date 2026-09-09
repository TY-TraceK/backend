package com.tracek.domain.visitVerification.presentation.controller.docs;

import com.tracek.domain.visitVerification.presentation.dto.VisitVerificationCancelResponse;
import com.tracek.domain.visitVerification.presentation.dto.request.VisitVerificationCreateRequest;
import com.tracek.domain.visitVerification.presentation.dto.request.VisitVerificationHistoriesSearchRequest;
import com.tracek.domain.visitVerification.presentation.dto.request.VisitVerificationStatusSearchRequest;
import com.tracek.domain.visitVerification.presentation.dto.response.VisitVerificationCreateResponse;
import com.tracek.domain.visitVerification.presentation.dto.response.VisitVerificationHistoriesResponse;
import com.tracek.domain.visitVerification.presentation.dto.response.VisitVerificationStatusSearchResponse;
import com.tracek.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "visitVerification", description = "방문 인증 관련 API")
public interface VisitVerificationControllerDocs {

    @Operation(
            summary = "관광지 방문인증 생성",
            description =
                    "로그인한 사용자가 특정 관광지-콘텐츠-아티스트 조합에 방문인증을 진행합니다. 동일 장소에 유효한 방문 인증가 이미 존재하면 중복으로 차단됩니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "방문 인증 생성 성공",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description =
                                "1. 요청 파라미터 유효성 검증 실패 (필수 필드 누락/공백)\n2. 이미 해당 관광지에 유효한 방문인증을 완료한 경우 (ALREADY_visitVerificationD)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "인증 토큰이 누락되었거나 유효하지 않은 경우 (UNAUTHORIZED)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description =
                                "요청한 관광지/아티스트/콘텐츠 매핑 정보를 찾을 수 없는 경우 (MAPPING_NOT_FOUND / visitVerification_TARGET_NOT_FOUND)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "409",
                        description = "동시에 중복 방문 인증 요청이 인입되어 DB 제약조건 충돌이 발생한 경우 (CONFLICT)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            })
    @PostMapping()
    @SecurityRequirement(name = "jwtAuth")
    ApiResponse<VisitVerificationCreateResponse> createVisitVerification(
            @Parameter(hidden = true) @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal,
            @Valid @RequestBody VisitVerificationCreateRequest request);

    @Operation(
            summary = "관광지 방문 인증 취소",
            description = "로그인한 사용자가 해당 관광지의 방문 인증을 취소합니다. 방문 인증 취소는 오늘 건에 대해서만 가능합니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "방문 인증 취소 성공 (이미 취소된 건일 경우 멱등하게 정상 응답 처리)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "400",
                        description =
                                "당일 생성된 방문 인증이 아니라서 취소할 수 없는 경우 (visitVerification_CANNOT_BE_CANCELLED)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "인증 토큰이 누락되었거나 유효하지 않은 경우 (UNAUTHORIZED)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "403",
                        description =
                                "방문 인증 소유자가 아닌 유저가 취소를 요청한 경우 (UNAUTHORIZED_visitVerification_ACCESS)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class))),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "존재하지 않는 방문 인증 ID로 취소를 요청한 경우 (visitVerification_NOT_FOUND)",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            })
    @SecurityRequirement(name = "jwtAuth")
    ApiResponse<VisitVerificationCancelResponse> cancelVisitVerification(
            @Parameter(hidden = true) @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal,
            @Valid @PathVariable Long visitVerificationId);

    @Operation(
            summary = "관광지의 나의 방문 인증 내역 확인",
            description = "로그인한 사용자가 해당 관광지의 방문 인증에 해당 날짜에 방문 인증를 했는지 확인합니다. 기본은 오늘 날짜로 조회합니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "방문 인증 조회 성공",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            })
    @SecurityRequirement(name = "jwtAuth")
    ApiResponse<VisitVerificationStatusSearchResponse> getMyvisitVerificationStatus(
            @Parameter(hidden = true) @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal,
            @PathVariable Long locationId,
            @Valid @Parameter(required = false) @ModelAttribute
                    VisitVerificationStatusSearchRequest visitVerificationStatusSearchRequest);

    @Operation(summary = "나의 방문 인증 내역 전체 조회", description = "내가 방문 인증한 내역에 대해 조회할 수 있습니다.")
    @ApiResponses(
            value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "방문 인증 조회 성공",
                        content = @Content(schema = @Schema(implementation = ApiResponse.class)))
            })
    @SecurityRequirement(name = "jwtAuth")
    ApiResponse<VisitVerificationHistoriesResponse> getMyvisitVerificationHistories(
            @Parameter(hidden = true) @AuthenticationPrincipal
                    com.tracek.global.security.authentication.AuthenticationPrincipal principal,
            @Valid @Parameter(required = false) @ParameterObject
                    VisitVerificationHistoriesSearchRequest visitVerificationHistoriesSearchRequest,
            @ParameterObject @PageableDefault(size = 10, direction = Sort.Direction.DESC)
                    Pageable pageable);
}
