package com.tracek.domain.content.domain.model;

import com.tracek.domain.location.domain.model.Location;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "episode_location",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "UK_episode_location",
                    columnNames = {"episode_id", "location_id"})
        })
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EpisodeLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    public static EpisodeLocation create(Episode episode, Location location) {
        return new EpisodeLocation(null, episode, location);
    }
}
