package com.tracek.domain.vote.domain.model;

import com.tracek.domain.vote.domain.enums.VoteStatus;
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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
    name = "vote",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_vote_owner_location_voted_at",
            columnNames = {"vote_owner", "location_id", "voted_at"}
        )
    }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Vote {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(nullable = false)
  private Long voteOwner;

  @Embedded
  private VoteTarget voteTarget;

  private LocalDate votedAt;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private VoteStatus voteStatus = VoteStatus.VALID;

  public Vote(Long voteOwner, VoteTarget voteTarget, LocalDate votedAt) {
    this.voteOwner = voteOwner;
    this.voteTarget = voteTarget;
    this.votedAt = votedAt;
  }

  public static Vote createVote(
      Long voteOwner, VoteTarget voteTarget) {
    return new Vote(voteOwner, voteTarget, LocalDate.now());
  }

  public void invalid() {
    this.voteStatus = VoteStatus.CANCELED;
  }
}
