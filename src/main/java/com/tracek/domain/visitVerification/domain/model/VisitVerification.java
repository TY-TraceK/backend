package com.tracek.domain.visitVerification.domain.model;

import com.tracek.domain.visitVerification.domain.enums.VisitVerificationStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "visit_verification",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_visitVerification_owner_location_valid",
                    columnNames = {"owner", "location_id", "valid_verified_at"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VisitVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long owner;

    @Embedded private VisitVerificationTarget verificationTarget;

    @Column(nullable = false)
    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private VisitVerificationStatus status = VisitVerificationStatus.VALID;

    @Column(
            name = "valid_verified_at",
            insertable = false,
            updatable = false,
            columnDefinition =
                    "DATE GENERATED ALWAYS AS (CASE WHEN status = 'VALID' THEN verified_at ELSE NULL END)")
    private LocalDate validVerifiedAt;

    public VisitVerification(
            Long owner, VisitVerificationTarget verificationTarget, LocalDateTime verifiedAt) {
        this.owner = owner;
        this.verificationTarget = verificationTarget;
        this.verifiedAt = verifiedAt;
    }

    public static VisitVerification createvisitVerification(
            Long visitVerificationOwner, VisitVerificationTarget visitVerificationTarget) {
        return new VisitVerification(
                visitVerificationOwner, visitVerificationTarget, LocalDateTime.now());
    }

    public void invalid() {
        this.status = VisitVerificationStatus.CANCELED;
    }
}
