package com.tracek.domain.ranking.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "content_location_ranking",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_location_content_ranking_location_artist",
                    columnNames = {"location_id", "content_id"})
        })
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentLocationVisitRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(name = "total_visit_verification_count", nullable = false)
    private long totalVerificationCount;

    private ContentLocationVisitRanking(Long locationId, Long contentId) {
        this.locationId = locationId;
        this.contentId = contentId;
        this.totalVerificationCount = 0L;
    }

    public static ContentLocationVisitRanking create(Long locationId, Long contentId) {
        return new ContentLocationVisitRanking(locationId, contentId);
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
