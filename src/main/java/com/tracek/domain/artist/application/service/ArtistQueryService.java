package com.tracek.domain.artist.application.service;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.domain.exception.ArtistErrorCode;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.artist.domain.repository.ArtistRepository;
import com.tracek.global.exception.CustomException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtistQueryService {
    private final ArtistRepository artistRepository; // 조합 (has-a) 필요한 메서드만 골라 위임

    public Artist getArtistEntity(Long artistId) {
        return artistRepository
                .findById(artistId)
                .orElseThrow(() -> new CustomException(ArtistErrorCode.ARTIST_NOT_FOUND));
    }

    public List<ArtistResult> getArtistsByIds(List<Long> artistIds) {
        if (artistIds == null || artistIds.isEmpty()) {
            return List.of();
        }
        List<Artist> artists = artistRepository.findAllByIds(artistIds);
        return artists.stream().map(ArtistResult::from).toList();
    }
}
