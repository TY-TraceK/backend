package com.tracek.domain.artist.application.service;

import com.tracek.domain.artist.application.ArtistQueryRepository;
import com.tracek.domain.artist.application.dto.ArtistSearchQuery;
import com.tracek.domain.artist.application.dto.ArtistSearchResult;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ArtistSearchQueryService {
    private final ArtistQueryRepository artistQueryRepository;

    public ArtistSearchResult searchArtists(ArtistSearchQuery query) {

        if (!StringUtils.hasText(query.getKeyword())) {
            return ArtistSearchResult.of(Collections.emptyList(), 0);
        }

        // hasNext 판별을 위한 N+1 조회
        int fetchSize = query.getSize() + 1;
        List<ArtistSearchResult.ArtistInfo> artists =
                artistQueryRepository.searchArtists(query, fetchSize);

        return ArtistSearchResult.of(artists, query.getSize());
    }
}
