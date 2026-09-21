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
            List.of("BIFF 광장", "청사포 철길 건너목", "부평깡통시장", "부산사직종합운동장 사직야구장", "이바구길사진관");

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
                            l.mainImageUrl.imageUrl,
                            case l.name
                                when '부산사직종합운동장 사직야구장' then '다양한 미디어를 만나보세요.'
                                when '부평깡통시장' then '다양한 미디어를 만나보세요.'
                                when 'BIFF 광장' then '<서울촌놈> 촬영 아티스트를 만나보세요.'
                                when '이바구길사진관' then '주변 장소를 둘러보세요.'
                                when '청사포 철길 건너목' then '다양한 여행지를 둘러보세요.'
                                else null
                            end
                        )
                        from Location l
                        where l.name in :names
                        """,
                        VerificationLocationCandidateResult.class)
                .setParameter("names", CANDIDATE_NAMES)
                .getResultList();
    }
}
