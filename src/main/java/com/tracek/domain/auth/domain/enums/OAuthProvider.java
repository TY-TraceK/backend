package com.tracek.domain.auth.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import java.util.Optional;

public enum OAuthProvider {
    KAKAO;

    @JsonCreator
    public static Optional<OAuthProvider> from(String provider) {
        if (provider == null || provider.isBlank()) {
            return Optional.empty();
        }
        return Arrays.stream(values()).filter(p -> p.name().equalsIgnoreCase(provider)).findFirst();
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
