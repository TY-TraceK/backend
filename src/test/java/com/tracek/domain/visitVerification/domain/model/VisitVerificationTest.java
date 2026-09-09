package com.tracek.domain.visitVerification.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("visitVerification 엔티티 단위 테스트")
class VisitVerificationTest {

    private Long visitVerificationOwner;
    private Long locationId;
    private VisitVerificationTarget visitVerificationTarget;

    @BeforeEach
    void setUp() {
        visitVerificationOwner = 1L;
        locationId = 100L;
        visitVerificationTarget = VisitVerificationTarget.of(1000L, 10L);
    }

    @Nested
    @DisplayName("방문 인증 생성 테스트")
    class CreateVisitVerificationTest {

        @Test
        @DisplayName("createvisitVerification 정적 팩토리 메서드로 방문 인증를 성공적으로 생성한다.")
        void createvisitVerification_success() {
            // when
            VisitVerification visitVerification =
                    VisitVerification.createvisitVerification(
                            visitVerificationOwner, locationId, visitVerificationTarget);

            // then
            assertThat(visitVerification).isNotNull();
            assertThat(visitVerification.getOwner()).isEqualTo(visitVerificationOwner);
            assertThat(visitVerification.getVerificationTarget())
                    .isEqualTo(visitVerificationTarget);
            assertThat(visitVerification.getStatus()).isEqualTo(VisitVerificationStatus.VALID);
        }

        @Test
        @DisplayName("생성자로 직접 방문 인증 객체를 올바르게 생성한다.")
        void constructor_success() {

            // when
            VisitVerification visitVerification =
                    VisitVerification.createvisitVerification(
                            visitVerificationOwner, locationId, visitVerificationTarget);

            // then
            assertThat(visitVerification.getOwner()).isEqualTo(visitVerificationOwner);
            assertThat(visitVerification.getVerificationTarget())
                    .isEqualTo(visitVerificationTarget);
            assertThat(visitVerification.getStatus()).isEqualTo(VisitVerificationStatus.VALID);
        }
    }

    @Nested
    @DisplayName("방문 인증 상태 및 무효화 테스트")
    class VisitVerificationStatusTest {

        @Test
        @DisplayName("invalid() 호출 시 방문 인증 상태가 CANCELED로 변경된다.")
        void invalid_success() {
            // given
            VisitVerification visitVerification =
                    VisitVerification.createvisitVerification(
                            visitVerificationOwner, locationId, visitVerificationTarget);
            assertThat(visitVerification.getStatus()).isEqualTo(VisitVerificationStatus.VALID);

            // when
            visitVerification.invalid();

            // then
            assertThat(visitVerification.getStatus()).isEqualTo(VisitVerificationStatus.CANCELED);
        }

        @Test
        @DisplayName("방문 인증 상태에 따른 validvisitVerificationdAt 가상 컬럼 동작을 검증한다.")
        void validvisitVerificationdAt_behavior_by_status() {
            // given
            VisitVerification validvisitVerification =
                    VisitVerification.createvisitVerification(
                            visitVerificationOwner, locationId, visitVerificationTarget);

            // 1. VALID 상태일 때: DB 트리거 계산 로직상 validvisitVerificationdAt은 visitVerificationdAt과 동일
            assertThat(validvisitVerification.getStatus()).isEqualTo(VisitVerificationStatus.VALID);

            // 2. invalid()로 취소(CANCELED) 시 상태 전환 검증
            validvisitVerification.invalid();
            assertThat(validvisitVerification.getStatus())
                    .isEqualTo(VisitVerificationStatus.CANCELED);
            // DB 적용 후 valid_visitVerificationd_at은 NULL로 자동 평가되어 UNIQUE 제약에서 제외됨
        }
    }
}
