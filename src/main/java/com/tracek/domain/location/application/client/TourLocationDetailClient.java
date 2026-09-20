package com.tracek.domain.location.application.client;

import com.tracek.domain.location.application.dto.TourLocationDetailResult;
import org.springframework.stereotype.Component;

@Component
public interface TourLocationDetailClient {

    TourLocationDetailResult getDetail(Long externalContentId);
}
