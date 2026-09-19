package com.tracek.domain.visitVerification.application.dto.result;

import java.util.List;

public record VerificationLocationResult(
        boolean isInBusan, List<VerificationLocationCandidateResult> locations) {

    public static VerificationLocationResult inBusan() {
        return new VerificationLocationResult(true, List.of());
    }

    public static VerificationLocationResult outsideBusan(
            List<VerificationLocationCandidateResult> locations) {
        return new VerificationLocationResult(false, locations);
    }
}
