package com.tracek.domain.fan.application.service.impl;

import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.fan.application.service.FanCommandService;
import com.tracek.domain.fan.domain.exception.FanErrorCode;
import com.tracek.domain.fan.domain.model.ArtistFan;
import com.tracek.domain.fan.domain.model.ArtistFanId;
import com.tracek.domain.fan.domain.model.ContentFan;
import com.tracek.domain.fan.domain.model.ContentFanId;
import com.tracek.domain.fan.domain.repository.ArtistFanRepository;
import com.tracek.domain.fan.domain.repository.ContentFanRepository;
import com.tracek.global.exception.CustomException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FanCommandServiceImpl implements FanCommandService {

    private final ArtistFanRepository artistFanRepository;
    private final ContentFanRepository contentFanRepository;
    private final ArtistQueryService artistQueryService;
    private final ContentQueryService contentQueryService;

    @Override
    public void createArtistFan(Long userId, Long artistId) {
        ArtistFanId id = new ArtistFanId(userId, artistId);
        artistQueryService.getArtistEntity(artistId);
        if (artistFanRepository.existsById(id)) {
            return;
        }
        try {
            artistFanRepository.saveAndFlush(ArtistFan.create(id));
        } catch (DataIntegrityViolationException ignored) {
        }
    }

    @Override
    public void createContentFan(Long userId, Long contentId) {
        contentQueryService.getContentEntity(contentId);
        ContentFanId id = new ContentFanId(userId, contentId);
        if (contentFanRepository.existsById(id)) {
            return;
        }

        try {
            contentFanRepository.saveAndFlush(ContentFan.create(id));
        } catch (DataIntegrityViolationException ignored) {
        }
    }

    @Override
    public void deleteArtistFan(Long userId, Long fanId) {
        ArtistFanId id = new ArtistFanId(userId, fanId);
        ArtistFan fan =
                artistFanRepository
                        .findById(id)
                        .orElseThrow(() -> new CustomException(FanErrorCode.FAN_NOT_FOUND));
        if (!Objects.equals(fan.getUserId(), userId)) {
            throw new CustomException(FanErrorCode.ACCESS_DINED);
        }
        artistFanRepository.delete(id);
    }

    @Override
    public void deleteContentFan(Long userId, Long contentId) {
        ContentFanId id = new ContentFanId(userId, contentId);
        ContentFan fan =
                contentFanRepository
                        .findById(id)
                        .orElseThrow(() -> new CustomException(FanErrorCode.FAN_NOT_FOUND));

        if (!Objects.equals(fan.getUserId(), userId)) {
            throw new CustomException(FanErrorCode.ACCESS_DINED);
        }
        contentFanRepository.delete(id);
    }
}
