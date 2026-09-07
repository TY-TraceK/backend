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
    name = "artist_location_ranking",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_location_artist_ranking_location_artist",
            columnNames = {"location_id", "artist_id"})
    })
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistLocationVisitRanking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "location_id", nullable = false)
  private Long locationId;

  @Column(name = "artist_id", nullable = false)
  private Long artistId;

  @Column(name = "total_visit_verification_count", nullable = false)
  private long totalVerificationCount;

  private ArtistLocationVisitRanking(Long locationId, Long artistId) {
    this.locationId = locationId;
    this.artistId = artistId;
    this.totalVerificationCount = 0L;
  }

  public static ArtistLocationVisitRanking create(Long locationId, Long artistId) {
    return new ArtistLocationVisitRanking(locationId, artistId);
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
