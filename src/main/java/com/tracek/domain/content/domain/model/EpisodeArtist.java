package com.tracek.domain.content.domain.model;

import com.tracek.domain.artist.domain.model.Artist;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "episode_artist",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "UK_episode_artist",
                    columnNames = {"episode_id", "artist_id"})
        })
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EpisodeArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "episode_id", nullable = false)
    private Episode episode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    public static EpisodeArtist create(Episode episode, Artist artist) {
        return new EpisodeArtist(null, episode, artist);
    }
}
