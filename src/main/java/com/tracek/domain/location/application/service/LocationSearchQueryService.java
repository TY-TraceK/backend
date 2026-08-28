package com.tracek.domain.location.application.service;

import com.tracek.domain.location.application.LocationQueryRepository;
import com.tracek.domain.location.application.dto.LocationSearchQuery;
import com.tracek.domain.location.application.dto.LocationSearchResult;
import com.tracek.domain.location.domain.model.LocationCategory;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationSearchQueryService {
    private final LocationQueryRepository locationQueryRepository;

    public LocationSearchResult searchLocations(LocationSearchQuery query) {

        if (!StringUtils.hasText(query.getKeyword())) {
            return LocationSearchResult.of(Collections.emptyList(), 0);
        }

        LocationCategory category = LocationCategory.from(query.getCategory());

        // hasNext를 위해 N+1 조회
        int fetchSize = query.getSize() + 1;
        List<LocationSearchResult.LocationInfo> locations =
                locationQueryRepository.searchLocations(query, category, fetchSize);

        return LocationSearchResult.of(locations, query.getSize());
    }
}
