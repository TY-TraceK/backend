package com.tracek.domain.ranking.application.dto.result;

import com.tracek.domain.ranking.domain.model.ContentCurationView;
import java.util.List;

public record ContentCurationResult(
        Long contentId,
        String contentTitle,
        long totalVerificationCount,
        List<String> locationNames) {

    public static ContentCurationResult from(ContentCurationView view) {
        return new ContentCurationResult(
                view.contentId(),
                view.contentTitle(),
                view.totalVerificationCount(),
                view.locationNames());
    }
}
