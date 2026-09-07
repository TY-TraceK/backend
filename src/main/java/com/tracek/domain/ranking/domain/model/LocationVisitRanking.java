package com.tracek.domain.ranking.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "location_ranking")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocationVisitRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_id", nullable = false, unique = true)
    private Long locationId;

    @Column(name = "total_visit_verification_count", nullable = false)
    private long totalVerificationCount;

    private LocationVisitRanking(Long locationId) {
        this.locationId = locationId;
        this.totalVerificationCount = 0L;
    }

    public static LocationVisitRanking create(Long locationId) {
        return new LocationVisitRanking(locationId);
    }

    public void increaseVerificationCount() {
        this.totalVerificationCount++;
    }

    public void decreaseVerificationCount() {
        if (this.totalVerificationCount > 0) {
            this.totalVerificationCount--;
        }
    }
}
