package com.tracek.domain.location.application.dto;

import com.tracek.domain.location.domain.model.LocationContentArtist;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class LocationContentArtistResult {
    private Long contentArtistLocationId;
    private Long locationId;
    private Long contentId;
    private Long artistId;

    public static LocationContentArtistResult from(LocationContentArtist mapping) {
        return new LocationContentArtistResult(
                mapping.getId(),
                mapping.getLocation().getId(),
                mapping.getContent().getId(),
                mapping.getArtist().getId());
    }
}
