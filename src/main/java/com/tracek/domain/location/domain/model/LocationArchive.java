package com.tracek.domain.location.domain.model;

import com.tracek.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "location_archive",
        uniqueConstraints = {
            @UniqueConstraint(
                    name = "uk_location_archive_user_location",
                    columnNames = {"user_id", "location_id"})
        })
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class LocationArchive extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "location_id", nullable = false)
    private Long locationId;

    private LocationArchive(Long userId, Long locationId) {
        this.userId = userId;
        this.locationId = locationId;
    }

    public static LocationArchive of(Long userId, Long locationId) {
        return new LocationArchive(userId, locationId);
    }
}
