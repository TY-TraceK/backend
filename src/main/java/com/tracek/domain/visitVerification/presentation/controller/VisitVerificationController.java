package com.tracek.domain.visitVerification.presentation.controller;

import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCancelCommand;
import com.tracek.domain.visitVerification.application.service.VisitVerificationCommandService;
import com.tracek.domain.visitVerification.application.service.VisitVerificationQueryService;
import com.tracek.domain.visitVerification.presentation.controller.docs.VisitVerificationControllerDocs;
import com.tracek.domain.visitVerification.presentation.dto.VisitVerificationCancelResponse;
import com.tracek.domain.visitVerification.presentation.dto.request.VisitVerificationCreateRequest;
import com.tracek.domain.visitVerification.presentation.dto.request.VisitVerificationHistoriesSearchRequest;
import com.tracek.domain.visitVerification.presentation.dto.request.VisitVerificationStatusSearchRequest;
import com.tracek.domain.visitVerification.presentation.dto.response.VisitVerificationCreateResponse;
import com.tracek.domain.visitVerification.presentation.dto.response.VisitVerificationHistoriesResponse;
import com.tracek.domain.visitVerification.presentation.dto.response.VisitVerificationStatusSearchResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/visitVerifications")
@RequiredArgsConstructor
public class VisitVerificationController implements VisitVerificationControllerDocs {

    private final VisitVerificationCommandService visitVerificationCommandService;
    private final VisitVerificationQueryService visitVerificationQueryService;

    @Override
    @PostMapping()
    public ApiResponse<VisitVerificationCreateResponse> createvisitVerification(
            AuthenticationPrincipal principal, VisitVerificationCreateRequest request) {
        return ApiResponse.success(
                GeneralSuccessCode.OK,
                VisitVerificationCreateResponse.from(
                        visitVerificationCommandService.createvisitVerification(
                                request.toCommand(principal.userId()))));
    }

    @Override
    @PatchMapping("/{visitVerificationId}")
    public ApiResponse<VisitVerificationCancelResponse> cancelvisitVerification(
            AuthenticationPrincipal principal, Long visitVerificationId) {
        return ApiResponse.success(
                GeneralSuccessCode.OK,
                VisitVerificationCancelResponse.from(
                        visitVerificationCommandService.cancelvisitVerification(
                                VisitVerificationCancelCommand.of(
                                        visitVerificationId, principal.userId()))));
    }

    @Override
    @GetMapping("/locations/{locationId}/me")
    public ApiResponse<VisitVerificationStatusSearchResponse> getMyvisitVerificationStatus(
            AuthenticationPrincipal principal,
            Long locationId,
            VisitVerificationStatusSearchRequest request) {
        return ApiResponse.success(
                GeneralSuccessCode.OK,
                VisitVerificationStatusSearchResponse.from(
                        visitVerificationQueryService.getMyvisitVerificationStatus(
                                request.toCondition(locationId, principal.userId()))));
    }

    @Override
    @GetMapping("/histories/me")
    public ApiResponse<VisitVerificationHistoriesResponse> getMyvisitVerificationHistories(
            AuthenticationPrincipal principal,
            VisitVerificationHistoriesSearchRequest request,
            Pageable pageable) {
        return ApiResponse.success(
                GeneralSuccessCode.OK,
                VisitVerificationHistoriesResponse.from(
                        visitVerificationQueryService.getMyHistories(
                                request.toCondition(principal.userId()), pageable)));
    }
}
