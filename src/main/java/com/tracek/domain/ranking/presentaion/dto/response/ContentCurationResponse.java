package com.tracek.domain.ranking.presentaion.dto.response;

import com.tracek.domain.ranking.application.dto.result.ContentCurationResult;
import java.util.List;

public record ContentCurationResponse(
        Long contentId,
        String contentTitle,
        long totalVerificationCount,
        List<String> locationNames) {

    public static ContentCurationResponse from(ContentCurationResult result) {
        return new ContentCurationResponse(
                result.contentId(),
                result.contentTitle(),
                result.totalVerificationCount(),
                result.locationNames());
    }
}
