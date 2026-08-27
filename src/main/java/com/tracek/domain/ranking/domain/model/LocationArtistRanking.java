package com.tracek.domain.ranking.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "location_artist_ranking",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_location_artist_ranking_location_artist",
                    columnNames = {"location_id", "artist_id"})
        })
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocationArtistRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    @Column(name = "artist_id", nullable = false)
    private Long artistId;

    @Column(name = "total_vote_count", nullable = false)
    private long totalVoteCount;

    @Column(name = "ranked_at", nullable = false)
    private LocalDateTime rankedAt;

    private LocationArtistRanking(Long locationId, Long artistId) {
        this.locationId = locationId;
        this.artistId = artistId;
        this.totalVoteCount = 0L;
        this.rankedAt = LocalDateTime.now();
    }

    public static LocationArtistRanking create(Long locationId, Long artistId) {
        return new LocationArtistRanking(locationId, artistId);
    }

    public void increaseVoteCount() {
        this.totalVoteCount++;
    }

    public void decreaseVoteCount() {
        this.totalVoteCount--;
    }
}
