package com.tracek.domain.visitVerification.infrastructure.persistence;

import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationHistoryCriteria;
import com.tracek.domain.visitVerification.domain.repository.VisitVerificationRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VisitVerificationRepositoryImpl implements VisitVerificationRepository {

    private final VisitVerificationJpaRepository visitVerificationJpaRepository;

    private final VisitVerificationQueryDslRepository visitVerificationQueryDslRepository;

    @Override
    public boolean hasAlreadyVerifiedLocation(Long userId, Long locationId) {
        return visitVerificationJpaRepository.existsByOwnerAndLocationIdAndStatus(
                userId, locationId, VisitVerificationStatus.VALID);
    }

    @Override
    public void deleteAllInBatch() {
        visitVerificationJpaRepository.deleteAllInBatch();
    }

    @Override
    public List<VisitVerification> findAll() {
        return visitVerificationJpaRepository.findAll();
    }

    @Override
    public VisitVerification save(VisitVerification visitVerification) {
        return visitVerificationJpaRepository.save(visitVerification);
    }

    @Override
    public Optional<VisitVerification> findById(Long visitVerificationId) {
        return visitVerificationJpaRepository.findById(visitVerificationId);
    }

    @Override
    public Optional<VisitVerification> findUserLocationVerifiedByDate(
            Long userId, Long locationId, LocalDate date) {
        return visitVerificationJpaRepository.findByOwnerAndLocationIdAndValidVerifiedAt(
                userId, locationId, date);
    }

    @Override
    public List<VisitVerification> findHistoriesByCriteria(
            VisitVerificationHistoryCriteria visitVerificationHistoryCriteria) {
        return visitVerificationQueryDslRepository.findHistoriesByCriteria(
                visitVerificationHistoryCriteria);
    }
}
