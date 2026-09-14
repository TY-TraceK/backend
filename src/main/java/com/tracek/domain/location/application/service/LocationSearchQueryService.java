package com.tracek.domain.location.application.service;

import com.tracek.domain.location.application.LocationQueryRepository;
import com.tracek.domain.location.application.dto.LocationSearchQuery;
import com.tracek.domain.location.application.dto.LocationSearchResult;
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

        // hasNext를 위해 N+1 조회
        int fetchSize = query.getSize() + 1;
        List<LocationSearchResult.LocationInfo> locations =
                locationQueryRepository.searchLocations(query, fetchSize);

        return LocationSearchResult.of(locations, query.getSize());
    }

    // 통합검색용 - 이름만 매칭, 커서 기반 페이징 (hasNext/lastId)
    public LocationSearchResult searchLocationsByName(LocationSearchQuery query) {

        if (!StringUtils.hasText(query.getKeyword())) {
            return LocationSearchResult.of(Collections.emptyList(), 0);
        }

        int fetchSize = query.getSize() + 1;
        List<LocationSearchResult.LocationInfo> locations =
                locationQueryRepository.searchLocationsByName(query, fetchSize);

        return LocationSearchResult.of(locations, query.getSize());
    }
}
