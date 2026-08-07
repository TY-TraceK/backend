package com.tracek.domain.user.domain.model;

import com.tracek.domain.user.domain.enums.OAuthProvider;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@Embeddable
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class OAuthInfo {

    @Column(nullable = false, unique = true)
    private long providerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OAuthProvider oAuthProvider;

    public static OAuthInfo register(Long providerId, OAuthProvider oAuthProvider) {
        return new OAuthInfo(providerId, oAuthProvider);
    }
}
