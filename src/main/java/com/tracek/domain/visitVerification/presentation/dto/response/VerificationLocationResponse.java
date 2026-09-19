package com.tracek.domain.visitVerification.presentation.dto.response;

import com.tracek.domain.visitVerification.application.dto.result.VerificationLocationCandidateResult;
import com.tracek.domain.visitVerification.application.dto.result.VerificationLocationResult;
import java.util.List;

public record VerificationLocationResponse(
        boolean isInBusan, List<VerificationLocationCandidateResponse> locations) {

    public static VerificationLocationResponse from(VerificationLocationResult result) {
        return new VerificationLocationResponse(
                result.isInBusan(),
                result.locations().stream()
                        .map(VerificationLocationCandidateResponse::from)
                        .toList());
    }

    public record VerificationLocationCandidateResponse(
            String name, double latitude, double longitude, String imageUrl) {

        private static VerificationLocationCandidateResponse from(
                VerificationLocationCandidateResult result) {
            return new VerificationLocationCandidateResponse(
                    result.name(), result.latitude(), result.longitude(), result.imageUrl());
        }
    }
}
