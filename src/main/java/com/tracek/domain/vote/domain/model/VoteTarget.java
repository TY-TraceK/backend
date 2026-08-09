package com.tracek.domain.vote.domain.model;

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
public class VoteTarget {

  @Column(nullable = false)
  private Long locationId;

  @Column(nullable = false)
  private Long locationContentArtistId;

  @Column(nullable = false)
  private Long artistId;

  @Column(nullable = false)
  private Long contentId;

  @Column(nullable = false, length = 150)
  private String voteTargetNameSnapShot;

  public static VoteTarget of(
      Long locationId,
      Long locationContentArtistId,
      Long artistId,
      Long contentId,
      String voteTargetNameSnapShot
  ) {
    return new VoteTarget(locationId, locationContentArtistId, artistId, contentId,
        voteTargetNameSnapShot);
  }
}
