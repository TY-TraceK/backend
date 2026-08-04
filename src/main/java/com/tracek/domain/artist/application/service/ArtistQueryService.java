package com.tracek.domain.artist.application.service;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.domain.exception.ArtistErrorCode;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.artist.domain.repository.ArtistRepository;
import com.tracek.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArtistQueryService {
    private final ArtistRepository artistRepository; // 조합 (has-a) 필요한 메서드만 골라 위임

    // 타 도메인에서 아티스트 정보가 필요할 때 참조하는 조회 메서드
    public ArtistResult getArtist(Long artistId) {
        Artist artist =
                artistRepository
                        .findById(artistId)
                        .orElseThrow(() -> new CustomException(ArtistErrorCode.ARTIST_NOT_FOUND));
        return ArtistResult.from(artist);
    }
}
