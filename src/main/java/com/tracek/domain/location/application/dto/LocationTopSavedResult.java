package com.tracek.domain.location.application.dto;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationTopSavedResult {

    private Long id;
    private String name;
    private String mainImageUrl;
    private Long totalVerificationCount;
    private List<String> relatedContentTitles; // 방문 인증 순 top3
    private Boolean isArchived;

    public static LocationTopSavedResult of(
            LocationSummaryResult location, List<String> relatedContentTitles, Boolean isArchived) {
        return new LocationTopSavedResult(
                location.getId(),
                location.getName(),
                location.getMainImageUrl(),
                location.getTotalVerificationCount(),
                relatedContentTitles,
                isArchived);
    }
}
