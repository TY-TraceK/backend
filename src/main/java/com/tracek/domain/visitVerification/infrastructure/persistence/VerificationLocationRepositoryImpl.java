package com.tracek.domain.visitVerification.infrastructure.persistence;

import com.tracek.domain.visitVerification.application.dto.result.VerificationLocationCandidateResult;
import com.tracek.domain.visitVerification.application.repository.VerificationLocationRepository;
import jakarta.persistence.EntityManager;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class VerificationLocationRepositoryImpl implements VerificationLocationRepository {

    private static final List<String> CANDIDATE_NAMES =
            List.of(
                    "마리앙플러스 부산광복점",
                    "남선창고터",
                    "부산 구 백제병원",
                    "브라운핸즈백제",
                    "영동밀면&돼지국밥",
                    "이바구길사진관");

    private final EntityManager entityManager;

    @Override
    public List<VerificationLocationCandidateResult> findVerificationLocationCandidates() {
        return entityManager
                .createQuery(
                        """
                        select new com.tracek.domain.visitVerification.application.dto.result.VerificationLocationCandidateResult(
                            l.name,
                            l.geoLocation.latitude,
                            l.geoLocation.longitude,
                            l.mainImageUrl.imageUrl
                        )
                        from Location l
                        where l.name in :names
                        """,
                        VerificationLocationCandidateResult.class)
                .setParameter("names", CANDIDATE_NAMES)
                .getResultList();
    }
}
