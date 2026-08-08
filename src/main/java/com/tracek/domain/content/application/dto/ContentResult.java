package com.tracek.domain.content.application.dto;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.location.application.dto.LocationResult;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ContentResult {
    private Long contentId;
    private String title;
    private String category;
    private String pictureUrl;
    private List<LocationResult> locations;
    private List<ArtistResult> artists;

    // 단건 기본 생성 (연관 콘텐츠 없이) 순환 참조 방지
    public static ContentResult from(Content content) {
        return new ContentResult(
                content.getId(),
                content.getTitle(),
                content.getCategory(),
                content.getPictureUrl().getImageUrl(),
                Collections.emptyList(),
                Collections.emptyList());
    }

    public static ContentResult of(
            Content content, List<LocationResult> locations, List<ArtistResult> artists) {
        return new ContentResult(
                content.getId(),
                content.getTitle(),
                content.getCategory(),
                content.getPictureUrl().getImageUrl(),
                locations == null ? Collections.emptyList() : locations,
                artists == null ? Collections.emptyList() : artists);
    }
}
