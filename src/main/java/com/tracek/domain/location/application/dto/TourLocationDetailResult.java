package com.tracek.domain.location.application.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TourLocationDetailResult {
    private String overview;
    private String tel;

    public static TourLocationDetailResult of(String overview, String tel) {
        return new TourLocationDetailResult(overview, tel);
    }
}
