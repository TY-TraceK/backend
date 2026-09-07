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

    @Column(nullable = false)
    private Long locationId;

    @Column(nullable = false)
    private Long locationContentArtistId;

    @Column(nullable = false)
    private Long artistId;

    @Column(nullable = false)
    private Long contentId;

    @Column(nullable = false, length = 150)
    private String visitVerificationTargetNameSnapShot;

    public static VisitVerificationTarget of(
            Long locationId,
            Long locationContentArtistId,
            Long artistId,
            Long contentId,
            String visitVerificationTargetNameSnapShot) {
        return new VisitVerificationTarget(
                locationId,
                locationContentArtistId,
                artistId,
                contentId,
                visitVerificationTargetNameSnapShot);
    }
}
