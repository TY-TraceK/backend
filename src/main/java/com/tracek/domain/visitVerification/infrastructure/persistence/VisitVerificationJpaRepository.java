package com.tracek.domain.visitVerification.infrastructure.persistence;

import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VisitVerificationJpaRepository extends JpaRepository<VisitVerification, Long> {

    boolean existsByOwnerAndVerificationTarget_LocationIdAndStatus(
            Long owner, Long locationId, VisitVerificationStatus status);

    Optional<VisitVerification> findByOwnerAndVerificationTarget_LocationIdAndValidVerifiedAt(
            Long owner, Long verificationTarget_locationId, LocalDate validVerifiedAt);
}
