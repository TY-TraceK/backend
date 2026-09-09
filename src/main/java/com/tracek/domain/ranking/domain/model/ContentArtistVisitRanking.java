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
        name = "content_artist_ranking",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_content_artist_ranking_location_artist",
                    columnNames = {"artist_id", "content_id"})
        })
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentArtistVisitRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long contentId;

    private Long artistId;

    @Column(name = "total_visit_verification_count", nullable = false)
    private long totalVerificationCount;

    private ContentArtistVisitRanking(Long contentId, Long artistId) {
        this.contentId = contentId;
        this.artistId = artistId;
        this.totalVerificationCount = 0L;
    }

    public static ContentArtistVisitRanking create(Long contentId, Long artistId) {
        return new ContentArtistVisitRanking(contentId, artistId);
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
