package com.tracek.domain.visitVerification.domain.repository;

import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationHistoryCriteria;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VisitVerificationRepository {

    boolean hasAlreadyVerifiedLocation(Long userId, Long locationId);

    void deleteAllInBatch();

    List<VisitVerification> findAll();

    VisitVerification save(VisitVerification visitVerification);

    Optional<VisitVerification> findById(Long visitVerificationId);

    Optional<VisitVerification> findUserLocationVerifiedByDate(
            Long userId, Long locationID, LocalDate date);

    Page<VisitVerification> findHistoriesByCriteria(
            VisitVerificationHistoryCriteria visitVerificationHistoryCriteria, Pageable pageable);
}
