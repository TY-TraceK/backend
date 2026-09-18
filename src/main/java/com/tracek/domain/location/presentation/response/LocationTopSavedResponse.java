package com.tracek.domain.location.presentation.response;

import com.tracek.domain.location.application.dto.LocationTopSavedResult;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class LocationTopSavedResponse {

    private Long id;
    private String name;
    private String mainImageUrl;
    private Long totalVerificationCount;
    private List<String> relatedContentTitles;
    private Boolean isArchived;

    public static LocationTopSavedResponse from(LocationTopSavedResult location) {
        return new LocationTopSavedResponse(
                location.getId(),
                location.getName(),
                location.getMainImageUrl(),
                location.getTotalVerificationCount(),
                location.getRelatedContentTitles(),
                location.getIsArchived());
    }
}
