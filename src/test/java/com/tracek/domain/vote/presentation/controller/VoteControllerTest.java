package com.tracek.domain.vote.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracek.domain.vote.application.dto.command.VoteCreateCommand;
import com.tracek.domain.vote.application.dto.result.VoteCreateResult;
import com.tracek.domain.vote.application.service.VoteCommandService;
import com.tracek.domain.vote.domain.exception.VoteErrorCode;
import com.tracek.domain.vote.presentation.dto.request.VoteCreateRequest;
import com.tracek.global.exception.CustomException;
import com.tracek.global.response.GeneralErrorCode;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import com.tracek.global.security.jwt.JwtTokenProvider;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VoteController.class)
class VoteControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private VoteCommandService voteService;

    @MockitoBean private JwtTokenProvider jwtTokenProvider;

    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        // 실제 AuthenticationPrincipal 레코드(userId, userName, role) 생성
        AuthenticationPrincipal principal = new AuthenticationPrincipal(1L, "yujin", "ROLE_USER");

        mockAuthentication =
                new UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities());
    }

    @Nested
    @DisplayName("POST /api/vote - 투표 생성 API")
    class CreateVoteTest {

        @Test
        @DisplayName("성공: 인증된 유저와 유효한 Request가 전달되면 200 OK와 함께 생성된 투표 정보가 반환된다.")
        void createVote_success() throws Exception {
            // given
            VoteCreateRequest request =
                    new VoteCreateRequest(100L, 1000L, "경복궁 | BTS | Run BTS Ep.100");

            VoteCreateResult mockResult =
                    new VoteCreateResult(10L, "VALID", LocalDateTime.of(2026, 8, 12, 12, 0, 0));

            given(voteService.createVote(any(VoteCreateCommand.class))).willReturn(mockResult);

            // when & then
            mockMvc.perform(
                            post("/api/vote")
                                    .with(authentication(mockAuthentication))
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.voteId").value(10L))
                    .andExpect(jsonPath("$.data.voteStatus").value("VALID"))
                    .andExpect(jsonPath("$.data.votedAt").exists());
        }

        @Test
        @DisplayName("실패 (@Valid): 필수 필드가 누락되면 400 Bad Request를 반환한다.")
        void createVote_fail_validation() throws Exception {
            // given: locationId 누락, voteTargetNameSnapShot 공백
            VoteCreateRequest invalidRequest = new VoteCreateRequest(null, 1000L, "   ");

            // when & then
            mockMvc.perform(
                            post("/api/vote")
                                    .with(authentication(mockAuthentication))
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패 (인증 누락): 인증 정보가 없는 비로그인 유저 요청 시 401 Unauthorized를 반환한다.")
        void createVote_fail_unauthorized() throws Exception {
            // given
            VoteCreateRequest request =
                    new VoteCreateRequest(100L, 1000L, "경복궁 | BTS | Run BTS Ep.100");

            // when & then: authentication 없이 요청
            mockMvc.perform(
                            post("/api/vote")
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("실패 (비즈니스 예외): 이미 투표한 유저의 요청은 ALREADY_VOTED 에러와 함께 400을 반환한다.")
        void createVote_fail_alreadyVoted_customException() throws Exception {
            // given
            VoteCreateRequest request =
                    new VoteCreateRequest(100L, 1000L, "경복궁 | BTS | Run BTS Ep.100");

            given(voteService.createVote(any(VoteCreateCommand.class)))
                    .willThrow(new CustomException(VoteErrorCode.ALREADY_VOTED));

            // when & then
            mockMvc.perform(
                            post("/api/vote")
                                    .with(authentication(mockAuthentication))
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(VoteErrorCode.ALREADY_VOTED.getCode()));
        }

        @Test
        @DisplayName("실패 (동시성/DB 무결성 충돌): DataIntegrityViolationException 발생 시 409 CONFLICT를 반환한다.")
        void createVote_fail_dataIntegrityViolation() throws Exception {
            // given
            VoteCreateRequest request =
                    new VoteCreateRequest(100L, 1000L, "경복궁 | BTS | Run BTS Ep.100");

            given(voteService.createVote(any(VoteCreateCommand.class)))
                    .willThrow(new DataIntegrityViolationException("Unique constraint violation"));

            // when & then
            mockMvc.perform(
                            post("/api/vote")
                                    .with(authentication(mockAuthentication))
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(GeneralErrorCode.CONFLICT.getCode()));
        }
    }
}
