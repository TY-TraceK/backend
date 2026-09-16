package com.tracek.domain.visitVerification.domain.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.UniqueConstraint;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class VisitVerificationTarget {

    @ElementCollection
    @CollectionTable(
            name = "visit_verification_target_artist",
            joinColumns = @JoinColumn(name = "visit_verification_id"),
            uniqueConstraints = {
                @UniqueConstraint(columnNames = {"visit_verification_id", "artist_id"})
            })
    @Column(name = "artist_id")
    private Set<Long> artistIds = new HashSet<>();

    @Column(nullable = false)
    private Long contentId;

    public static VisitVerificationTarget of(Collection<Long> artistIds, Long contentId) {
        return new VisitVerificationTarget(new HashSet<>(artistIds), contentId);
    }
}
