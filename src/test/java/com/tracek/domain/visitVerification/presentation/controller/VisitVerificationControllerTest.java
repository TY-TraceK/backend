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
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesIndividualResult;
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
import java.util.List;
import java.util.Map;
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
        AuthenticationPrincipal principal = new AuthenticationPrincipal(1L, "yujin", "ROLE_USER");

        mockAuthentication =
                new UsernamePasswordAuthenticationToken(
                        principal, null, principal.getAuthorities());
    }

    @Nested
    @DisplayName("POST /api/visitVerifications - 방문 인증 생성 API")
    class CreateVisitVerificationTest {

        @Test
        @DisplayName("성공: 인증된 유저와 유효한 Request가 전달되면 200 OK와 함께 생성된 방문 인증 정보가 반환된다.")
        void createVisitVerification_success() throws Exception {
            // given: locationId, contentId, artistId, latitude, longitude
            VisitVerificationCreateRequest request =
                    new VisitVerificationCreateRequest(100L, 20L, 10L, 35.123456, 128.123456);

            VisitVerificationCreateResult mockResult =
                    new VisitVerificationCreateResult(
                            10L, "VALID", LocalDateTime.of(2026, 8, 12, 12, 0, 0));

            given(
                            visitVerificationService.createVisitVerification(
                                    any(VisitVerificationCreateCommand.class)))
                    .willReturn(mockResult);

            // when & then
            mockMvc.perform(
                            post("/api/visit-verifications")
                                    .with(authentication(mockAuthentication))
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.visitVerificationId").value(10L))
                    .andExpect(jsonPath("$.data.visitVerificationStatus").value("VALID"))
                    .andExpect(jsonPath("$.data.visitVerifiedAt").exists());
        }

        @Test
        @DisplayName("실패 (@Valid): 필수 필드가 누락되면 400 Bad Request를 반환한다.")
        void createVisitVerification_fail_validation() throws Exception {
            // given: locationId를 null로 설정하여 검증 실패 유도
            VisitVerificationCreateRequest invalidRequest =
                    new VisitVerificationCreateRequest(null, 20L, 10L, 35.123456, 128.123456);

            // when & then
            mockMvc.perform(
                            post("/api/visit-verifications")
                                    .with(authentication(mockAuthentication))
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패 (인증 누락): 인증 정보가 없는 비로그인 유저 요청 시 401 Unauthorized를 반환한다.")
        void createVisitVerification_fail_unauthorized() throws Exception {
            // given
            VisitVerificationCreateRequest request =
                    new VisitVerificationCreateRequest(100L, 20L, 10L, 35.123456, 128.123456);

            // when & then
            mockMvc.perform(
                            post("/api/visit-verifications")
                                    .with(csrf())
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("실패 (비즈니스 예외): 이미 방문 인증한 유저가 다시 요청하면 400 Bad Request를 반환한다.")
        void createVisitVerification_fail_alreadyVerified_customException() throws Exception {

            // given
            VisitVerificationCreateRequest request =
                    new VisitVerificationCreateRequest(100L, 20L, 10L, 35.123456, 128.123456);

            given(
                            visitVerificationService.createVisitVerification(
                                    any(VisitVerificationCreateCommand.class)))
                    .willThrow(new CustomException(VisitVerificationErrorCode.ALREADY_VERIFIED));

            // when & then
            mockMvc.perform(
                            post("/api/visit-verifications")
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
        void createVisitVerification_fail_dataIntegrityViolation() throws Exception {

            // given
            VisitVerificationCreateRequest request =
                    new VisitVerificationCreateRequest(100L, 20L, 10L, 35.123456, 128.123456);

            given(
                            visitVerificationService.createVisitVerification(
                                    any(VisitVerificationCreateCommand.class)))
                    .willThrow(new DataIntegrityViolationException("Unique constraint violation"));

            // when & then
            mockMvc.perform(
                            post("/api/visit-verifications")
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
    @DisplayName("GET /api/visitVerifications/locations/{locationId}/me - 나의 방문 인증 상태 조회 API")
    class GetMyVisitVerificationStatusControllerTest {

        @Test
        @DisplayName("성공: 특정 장소의 나의 방문 인증 상태를 정상 조회한다.")
        void getMyVisitVerificationStatus_success() throws Exception {
            // given
            Long locationId = 100L;

            VisitVerificationStatusSearchResult mockResult =
                    new VisitVerificationStatusSearchResult(true, 42L, LocalDate.of(2026, 8, 19));

            given(
                            visitVerificationQueryService.getMyVisitVerificationStatus(
                                    any(VisitVerificationStatusSearchCondition.class)))
                    .willReturn(mockResult);

            // when & then
            mockMvc.perform(
                            get("/api/locations/{locationId}/visit-verifications", locationId)
                                    .with(authentication(mockAuthentication))
                                    .param("targetDate", "2026-08-19"))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.isVisitVerified").value(true))
                    .andExpect(jsonPath("$.data.visitVerificationId").value(42L));
        }
    }

    @Nested
    @DisplayName("GET /api/visitVerifications/histories - 나의 방문 인증 이력 조회 API")
    class GetMyVisitVerificationHistoriesControllerTest {

        @Test
        @DisplayName("성공: 조건과 페이징 정보로 방문 인증 이력을 조회한다.")
        void getMyVisitVerificationHistories_success() throws Exception {
            // given
            Map<LocalDate, List<VisitVerificationHistoriesIndividualResult>> dummyHistories =
                    Map.of(LocalDate.of(2026, 8, 19), List.of());

            VisitVerificationHistoriesResult mockResult =
                    new VisitVerificationHistoriesResult(dummyHistories, false, null);

            given(
                            visitVerificationQueryService.getMyHistories(
                                    any(VisitVerificationHistoriesSearchCondition.class)))
                    .willReturn(mockResult);

            // when & then
            mockMvc.perform(
                            get("/api/users/me/visit-verifications")
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
        void getMyVisitVerificationHistories_fail_invalidDateRange() throws Exception {

            // when & then
            mockMvc.perform(
                            get("/api/users/me/visit-verifications")
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
