package com.tracek.domain.ranking.domain.model;

import java.util.List;

public record ContentCurationView(
        Long contentId,
        String contentTitle,
        long totalVerificationCount,
        List<String> locationNames) {}
