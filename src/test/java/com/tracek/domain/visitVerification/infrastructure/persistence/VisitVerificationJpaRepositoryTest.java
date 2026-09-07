package com.tracek.domain.visitVerification.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationTarget;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class VisitVerificationJpaRepositoryTest {

    private Long visitVerificationOwner;
    private VisitVerificationTarget visitVerificationTarget;

    @Autowired private VisitVerificationJpaRepository visitVerificationJpaRepository;

    @Autowired private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        visitVerificationOwner = 1L;
        visitVerificationTarget =
                VisitVerificationTarget.of(100L, 1000L, 10L, 20L, "경복궁 | BTS | Run BTS Ep.100");
    }

    @Test
    @DisplayName("DB에 저장 하면 가상 칼럼이 생성되고 취소(invalid)하면 validvisitVerificationdAt 가상 컬럼이 NULL이 된다.")
    void validvisitVerificationdAt_generated_column_test() {
        // given
        VisitVerification visitVerification =
                VisitVerification.createvisitVerification(
                        visitVerificationOwner, visitVerificationTarget);
        VisitVerification savedvisitVerification =
                visitVerificationJpaRepository.save(visitVerification);

        entityManager.flush();
        entityManager.clear();

        // 1. VALID 저장 시 validvisitVerificationdAt은 visitVerificationdAt 날짜와 동일하게 저장됨
        VisitVerification foundvisitVerification =
                visitVerificationJpaRepository
                        .findById(savedvisitVerification.getId())
                        .orElseThrow();
        assertThat(foundvisitVerification.getValidVerifiedAt()).isEqualTo(LocalDate.now());

        // 2. CANCELED 로 변경 후 DB flush
        foundvisitVerification.invalid();
        entityManager.flush();
        entityManager.clear();

        // 3. 재조회 시 DB가 valid_visitVerificationd_at을 NULL로 평가했는지 검증
        VisitVerification canceledvisitVerification =
                visitVerificationJpaRepository
                        .findById(savedvisitVerification.getId())
                        .orElseThrow();
        assertThat(canceledvisitVerification.getStatus())
                .isEqualTo(VisitVerificationStatus.CANCELED);
        assertThat(canceledvisitVerification.getValidVerifiedAt()).isNull();
    }

    @Test
    @DisplayName("동일 유저가 동일 장소에 VALID 상태로 중복 저장 시 DB 유니크 제약조건 위반 예외가 발생한다.")
    void duplicate_visitVerification_throws_DataIntegrityViolationException() {
        // given: 1차 투표 정상 저장
        VisitVerification visitVerification1 =
                VisitVerification.createvisitVerification(
                        visitVerificationOwner, visitVerificationTarget);
        visitVerificationJpaRepository.save(visitVerification1);
        entityManager.flush();

        // when & then: 동일한 조건의 2차 투표 저장 시 save() 호출 시점에 예외 발생 검증
        VisitVerification visitVerification2 =
                VisitVerification.createvisitVerification(
                        visitVerificationOwner, visitVerificationTarget);

        assertThatThrownBy(() -> visitVerificationJpaRepository.saveAndFlush(visitVerification2))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("기존 투표를 CANCELED(invalid) 처리하면 동일 유저가 동일 장소에 다시 유효한 투표를 저장할 수 있다.")
    void revisitVerification_success_after_cancellation() {
        // given: 1차 투표 후 취소 처리
        VisitVerification visitVerification1 =
                VisitVerification.createvisitVerification(
                        visitVerificationOwner, visitVerificationTarget);
        visitVerificationJpaRepository.save(visitVerification1);
        entityManager.flush();

        visitVerification1.invalid();
        entityManager.flush();
        entityManager.clear();

        // when: 2차 재투표 저장
        VisitVerification revisitVerification =
                VisitVerification.createvisitVerification(
                        visitVerificationOwner, visitVerificationTarget);
        VisitVerification savedRevisitVerification =
                visitVerificationJpaRepository.save(revisitVerification);
        entityManager.flush();
        entityManager.clear();

        // then: 중복 예외 없이 정상 저장되고 validvisitVerificationdAt이 세팅됨
        VisitVerification foundRevisitVerification =
                visitVerificationJpaRepository
                        .findById(savedRevisitVerification.getId())
                        .orElseThrow();
        assertThat(foundRevisitVerification.getStatus()).isEqualTo(VisitVerificationStatus.VALID);
    }
}
