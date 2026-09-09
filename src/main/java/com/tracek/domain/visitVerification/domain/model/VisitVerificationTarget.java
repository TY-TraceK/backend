package com.tracek.domain.visitVerification.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VisitVerificationTarget {

    @Column() private Long artistId;

    @Column(nullable = false)
    private Long contentId;

    public static VisitVerificationTarget of(Long artistId, Long contentId) {
        return new VisitVerificationTarget(artistId, contentId);
    }
}
