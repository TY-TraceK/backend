package com.tracek.domain.visitVerification.application.dto.result;

public record VerificationLocationCandidateResult(
        String name, double latitude, double longitude, String imageUrl, String description) {}
