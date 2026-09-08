package com.tracek.domain.artist.application.dto;

import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.location.application.dto.LocationResult;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ArtistResult {
    private Long id;
    private String name;
    private String alias;
    private String pictureUrl;
    private Long groupId;
    private Boolean isGroup;
    private Long fanCount;
    private Long totalVerificationCount;
    private List<LocationResult> locations;
    private List<ContentResult> contents;

    // 단건 기본 생성 (연관 콘텐츠 없이) 순환 참조 방지
    public static ArtistResult from(Artist artist) {
        return new ArtistResult(
                artist.getId(),
                artist.getName(),
                artist.getAlias(),
                artist.getPictureUrl().getImageUrl(),
                artist.getGroup() == null ? null : artist.getGroup().getId(),
                artist.getGroup() != null,
                0L,
                0L,
                Collections.emptyList(),
                Collections.emptyList());
    }

    public static ArtistResult of(
            Artist artist, List<LocationResult> locations, List<ContentResult> contents) {
        return new ArtistResult(
                artist.getId(),
                artist.getName(),
                artist.getAlias(),
                artist.getPictureUrl().getImageUrl(),
                artist.getGroup() == null ? null : artist.getGroup().getId(),
                artist.getGroup() != null,
                artist.getFanCount(),
                artist.getTotalVerificationCount(),
                locations == null ? Collections.emptyList() : locations,
                contents == null ? Collections.emptyList() : contents);
    }
}
