package com.tracek.domain.location.domain.model;

import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.domain.model.Content;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(
        name = "location_content_artist",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "UK_location_content_artist",
                    columnNames = {"location_id", "content_id", "artist_id"})
        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationContentArtist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    public static LocationContentArtist create(Location location, Content content, Artist artist) {
        return new LocationContentArtist(null, location, content, artist);
    }
}
