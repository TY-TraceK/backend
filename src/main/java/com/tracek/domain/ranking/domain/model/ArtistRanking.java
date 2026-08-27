package com.tracek.domain.ranking.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "artist_ranking")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistRanking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long artistId;

    @Column(nullable = false)
    private long totalVoteCount;

    @Column(nullable = false)
    private LocalDateTime rankedAt;

    private ArtistRanking(Long artistId) {
        this.artistId = artistId;
        this.totalVoteCount = 0L;
        this.rankedAt = LocalDateTime.now();
    }

    public static ArtistRanking create(Long artistId) {
        return new ArtistRanking(artistId);
    }

    public void increaseVoteCount() {
        this.totalVoteCount++;
    }

    public void decreaseVoteCount() {
        if (this.totalVoteCount > 0) {
            this.totalVoteCount--;
        }
    }
}
