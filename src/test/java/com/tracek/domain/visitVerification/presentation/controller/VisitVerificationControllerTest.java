package com.tracek.domain.visitVerification.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCreateCommand;
import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationHistoriesSearchCondition;
import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationStatusSearchCondition;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCreateResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationStatusSearchResult;
import com.tracek.domain.visitVerification.application.service.VisitVerificationCommandService;
import com.tracek.domain.visitVerification.application.service.VisitVerificationQueryService;
import com.tracek.domain.visitVerification.domain.exception.VisitVerificationErrorCode;
import com.tracek.domain.visitVerification.presentation.dto.request.VisitVerificationCreateRequest;
import com.tracek.global.exception.CustomException;
import com.tracek.global.response.GeneralErrorCode;
import com.tracek.global.security.authentication.AuthenticationPrincipal;
import com.tracek.global.security.jwt.JwtTokenProvider;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VisitVerificationController.class)
class VisitVerificationControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private VisitVerificationCommandService visitVerificationService;

    @MockitoBean private VisitVerificationQueryService visitVerificationQueryService;

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
    @DisplayName("POST /api/visitVerifications - 투표 생성 API")
    class CreateVisitVerificationTest {

        @Test
        @DisplayName("성공: 인증된 유저와 유효한 Request가 전달되면 200 OK와 함께 생성된 투표 정보가 반환된다.")
        void createvisitVerification_success() throws Exception {
            // given
            VisitVerificationCreateRequest request =
                    new VisitVerificationCreateRequest(100L, 1000L, "경복궁 | BTS | Run BTS Ep.100");

            VisitVerificationCreateResult mockResult =
                    new VisitVerificationCreateResult(
                            10L, "VALID", LocalDateTime.of(2026, 8, 12, 12, 0, 0));

            given(
                            visitVerificationService.createvisitVerification(
                                    any(VisitVerificationCreateCommand.class)))
                    .willReturn(mockResult);

            // when & then
            mockMvc.perform(
                            post("/api/visitVerifications")
                                    .with(authentication(mockAuthentication))
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.visitVerificationId").value(10L))
                    .andExpect(jsonPath("$.data.visitVerificationStatus").value("VALID"))
                    .andExpect(jsonPath("$.data.visitVerificationdAt").exists());
        }

        @Test
        @DisplayName("실패 (@Valid): 필수 필드가 누락되면 400 Bad Request를 반환한다.")
        void createvisitVerification_fail_validation() throws Exception {
            // given: locationId 누락, visitVerificationTargetNameSnapShot 공백
            VisitVerificationCreateRequest invalidRequest =
                    new VisitVerificationCreateRequest(null, 1000L, "   ");

            // when & then
            mockMvc.perform(
                            post("/api/visitVerifications")
                                    .with(authentication(mockAuthentication))
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패 (인증 누락): 인증 정보가 없는 비로그인 유저 요청 시 401 Unauthorized를 반환한다.")
        void createvisitVerification_fail_unauthorized() throws Exception {
            // given
            VisitVerificationCreateRequest request =
                    new VisitVerificationCreateRequest(100L, 1000L, "경복궁 | BTS | Run BTS Ep.100");

            // when & then: authentication 없이 요청
            mockMvc.perform(
                            post("/api/visitVerifications")
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("실패 (비즈니스 예외): 이미 투표한 유저의 요청은 ALREADY_visitVerificationD 에러와 함께 400을 반환한다.")
        void createvisitVerification_fail_alreadyvisitVerificationd_customException()
                throws Exception {
            // given
            VisitVerificationCreateRequest request =
                    new VisitVerificationCreateRequest(100L, 1000L, "경복궁 | BTS | Run BTS Ep.100");

            given(
                            visitVerificationService.createvisitVerification(
                                    any(VisitVerificationCreateCommand.class)))
                    .willThrow(new CustomException(VisitVerificationErrorCode.ALREADY_VERIFIED));

            // when & then
            mockMvc.perform(
                            post("/api/visitVerifications")
                                    .with(authentication(mockAuthentication))
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(
                            jsonPath("$.code")
                                    .value(VisitVerificationErrorCode.ALREADY_VERIFIED.getCode()));
        }

        @Test
        @DisplayName("실패 (동시성/DB 무결성 충돌): DataIntegrityViolationException 발생 시 409 CONFLICT를 반환한다.")
        void createvisitVerification_fail_dataIntegrityViolation() throws Exception {
            // given
            VisitVerificationCreateRequest request =
                    new VisitVerificationCreateRequest(100L, 1000L, "경복궁 | BTS | Run BTS Ep.100");

            given(
                            visitVerificationService.createvisitVerification(
                                    any(VisitVerificationCreateCommand.class)))
                    .willThrow(new DataIntegrityViolationException("Unique constraint violation"));

            // when & then
            mockMvc.perform(
                            post("/api/visitVerifications")
                                    .with(authentication(mockAuthentication))
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(GeneralErrorCode.CONFLICT.getCode()));
        }
    }

    @Nested
    @DisplayName("GET /api/locations/{locationId}/me - 나의 투표 상태 조회 API")
    class GetMyVisitVerificationStatusControllerTest {

        @Test
        @DisplayName("성공: 특정 장소의 나의 투표 상태를 정상 조회한다.")
        void getMyvisitVerificationStatus_success() throws Exception {
            // given
            Long locationId = 100L;
            VisitVerificationStatusSearchResult mockResult =
                    new VisitVerificationStatusSearchResult(true, 42L, LocalDate.of(2026, 8, 19));

            given(
                            visitVerificationQueryService.getMyvisitVerificationStatus(
                                    any(VisitVerificationStatusSearchCondition.class)))
                    .willReturn(mockResult);

            // when & then
            mockMvc.perform(
                            get("/api/visitVerifications/locations/{locationId}/me", locationId)
                                    .with(authentication(mockAuthentication))
                                    .param("targetDate", "2026-08-19"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.isvisitVerificationd").value(true))
                    .andExpect(jsonPath("$.data.visitVerificationId").value(42L));
        }
    }

    @Nested
    @DisplayName("GET /api/visitVerifications/histories - 나의 투표 이력 조회 API")
    class GetMyVisitVerificationHistoriesControllerTest {

        @Test
        @DisplayName("성공: 조건과 페이징 정보로 투표 이력을 조회한다.")
        void getMyvisitVerificationHistories_success() throws Exception {
            // given
            VisitVerificationHistoriesResult mockResult =
                    new VisitVerificationHistoriesResult(Page.empty());

            given(
                            visitVerificationQueryService.getMyHistories(
                                    any(VisitVerificationHistoriesSearchCondition.class), any()))
                    .willReturn(mockResult);

            // when & then
            mockMvc.perform(
                            get("/api/visitVerifications/histories/me")
                                    .with(authentication(mockAuthentication))
                                    .param("startDate", "2026-08-01")
                                    .param("endDate", "2026-09-01")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").exists());
        }

        @Test
        @DisplayName("실패 (@AssertTrue): 시작 날짜가 종료 날짜보다 늦으면 400 Bad Request를 반환한다.")
        void getMyvisitVerificationHistories_fail_invalidDateRange() throws Exception {
            // given: startDate가 endDate보다 늦은 경우 (검증 실패 유발)
            // when & then
            mockMvc.perform(
                            get("/api/visitVerifications/histories/me")
                                    .with(authentication(mockAuthentication))
                                    .param("startDate", "2026-09-01")
                                    .param("endDate", "2026-08-01")
                                    .param("page", "0")
                                    .param("size", "20"))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
