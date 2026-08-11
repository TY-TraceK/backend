package com.tracek.domain.location.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "location_likes",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_location_like_user_location",
                    columnNames = {"user_id", "location_id"})
        })
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class LocationLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    private LocationLike(Long userId, Long locationId) {
        this.userId = userId;
        this.locationId = locationId;
    }

    public static LocationLike of(Long userId, Long locationId) {
        return new LocationLike(userId, locationId);
    }
}
