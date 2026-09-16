package com.tracek.domain.fan.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "artist_fan")
@IdClass(ArtistFanId.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ArtistFan {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Id
    @Column(name = "artist_id", nullable = false)
    private Long artistId;

    private ArtistFan(Long userId, Long artistId) {
        this.userId = userId;
        this.artistId = artistId;
    }

    public static ArtistFan create(ArtistFanId id) {
        return new ArtistFan(id.getUserId(), id.getArtistId());
    }
}
