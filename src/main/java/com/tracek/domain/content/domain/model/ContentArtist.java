package com.tracek.domain.content.domain.model;

import com.tracek.domain.artist.domain.model.Artist;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "content_artist",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "UK_content_artist",
                    columnNames = {"content_id", "artist_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ContentArtist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    private Boolean isFixed; // guest이면 False, 고정이면 True

    public static ContentArtist create(Content content, Artist artist, Boolean isFixed) {
        return new ContentArtist(null, content, artist, isFixed);
    }
}
