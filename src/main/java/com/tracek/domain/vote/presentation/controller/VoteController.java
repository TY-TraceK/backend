package com.tracek.domain.vote.presentation.controller;

import com.tracek.domain.vote.application.dto.command.VoteCancelCommand;
import com.tracek.domain.vote.application.service.VoteCommandService;
import com.tracek.domain.vote.application.service.VoteQueryService;
import com.tracek.domain.vote.presentation.controller.docs.VoteControllerDocs;
import com.tracek.domain.vote.presentation.dto.VoteCancelResponse;
import com.tracek.domain.vote.presentation.dto.request.VoteCreateRequest;
import com.tracek.domain.vote.presentation.dto.request.VoteStatusSearchRequest;
import com.tracek.domain.vote.presentation.dto.response.VoteCreateResponse;
import com.tracek.domain.vote.presentation.dto.response.VoteStatusSearchResponse;
import com.tracek.global.response.ApiResponse;
import com.tracek.global.response.GeneralSuccessCode;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController implements VoteControllerDocs {

    private final VoteCommandService voteCommandService;
    private final VoteQueryService voteQueryService;

    @Override
    @PostMapping()
    public ApiResponse<VoteCreateResponse> createVote(
            AuthenticationPrincipal principal, VoteCreateRequest request) {
        return ApiResponse.success(
                GeneralSuccessCode.OK,
                VoteCreateResponse.from(
                        voteCommandService.createVote(request.toCommand(principal.userId()))));
    }

    @Override
    @PatchMapping("/{voteId}")
    public ApiResponse<VoteCancelResponse> cancelVote(
            AuthenticationPrincipal principal, Long voteId) {
        return ApiResponse.success(
                GeneralSuccessCode.OK,
                VoteCancelResponse.from(
                        voteCommandService.cancelVote(
                                VoteCancelCommand.of(voteId, principal.userId()))));
    }

    @Override
    @GetMapping("/locations/{locationId}/me")
    public ApiResponse<VoteStatusSearchResponse> getMyVoteStatus(
            AuthenticationPrincipal principal, Long locationId, VoteStatusSearchRequest request) {
        return ApiResponse.success(
                GeneralSuccessCode.OK,
                VoteStatusSearchResponse.from(
                        voteQueryService.getMyVoteStatus(
                                request.toCondition(locationId, principal.userId()))));
    }
}
