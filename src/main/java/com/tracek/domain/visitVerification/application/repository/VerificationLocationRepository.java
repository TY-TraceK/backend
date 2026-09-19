package com.tracek.domain.visitVerification.application.repository;

import com.tracek.domain.visitVerification.application.dto.result.VerificationLocationCandidateResult;
import java.util.List;

public interface VerificationLocationRepository {

    List<VerificationLocationCandidateResult> findVerificationLocationCandidates();
}
