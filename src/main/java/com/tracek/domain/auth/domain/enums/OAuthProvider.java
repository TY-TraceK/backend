package com.tracek.domain.auth.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum OAuthProvider {
    KAKAO;

    @JsonCreator
    public static OAuthProvider from(String value) {
        if (value == null) {
            return null;
        }
        return OAuthProvider.valueOf(value.toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return name().toLowerCase();
    }
}
