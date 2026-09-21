package com.tracek.domain.visitVerification.application.dto.result;

import lombok.Builder;

@Builder
public record VerificationLocationCandidateResult(
        String name, double latitude, double longitude, String imageUrl, String description) {}
