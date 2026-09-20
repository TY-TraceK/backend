package com.tracek.domain.location.application.client;

import com.tracek.domain.location.application.dto.TourImageResult;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public interface TourImageClient {

    List<TourImageResult> getImages(Long externalContentId);
}
