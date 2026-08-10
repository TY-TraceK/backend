package com.tracek.domain.artist.presentation.response;

import com.tracek.domain.artist.application.dto.ArtistSummaryResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ArtistSummaryResponse {

    private Long id;
    private String name;
    private String alias;
    private String pictureUrl;
    private Long groupId;

    public static ArtistSummaryResponse from(ArtistSummaryResult artist) {
        return new ArtistSummaryResponse(
                artist.getId(),
                artist.getName(),
                artist.getAlias(),
                artist.getPictureUrl(),
                artist.getGroupId());
    }
}
