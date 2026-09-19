package com.tracek.domain.visitVerification.application.dto.result;

import java.util.List;

public record VerificationLocationResult(
        boolean isInBusan, List<VerificationLocationCandidateResult> locations) {

    public static VerificationLocationResult outsideBusan() {
        return new VerificationLocationResult(false, List.of());
    }

    public static VerificationLocationResult inBusan(
            List<VerificationLocationCandidateResult> locations) {
        return new VerificationLocationResult(true, locations);
    }
}
