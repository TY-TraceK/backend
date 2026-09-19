package com.tracek.domain.fan.application.service;

import com.tracek.domain.artist.application.dto.ArtistResult;
import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.content.application.dto.ContentResult;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.fan.application.dto.result.ArtistFanViewResult;
import com.tracek.domain.fan.application.dto.result.ContentFanViewResult;
import com.tracek.domain.fan.application.dto.result.FanTargetResult;
import com.tracek.domain.fan.application.dto.result.MyFanResult;
import com.tracek.domain.fan.domain.model.ArtistFan;
import com.tracek.domain.fan.domain.model.ArtistFanId;
import com.tracek.domain.fan.domain.model.ContentFan;
import com.tracek.domain.fan.domain.model.ContentFanId;
import com.tracek.domain.fan.domain.repository.ArtistFanRepository;
import com.tracek.domain.fan.domain.repository.ContentFanRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FanQueryServiceImpl implements FanQueryService {

    private final ContentFanRepository contentFanRepository;
    private final ContentQueryService contentQueryService;
    private final ArtistFanRepository artistFanRepository;
    private final ArtistQueryService artistQueryService;

    @Override
    public MyFanResult getMyFanTargets(Long userId) {
        List<ContentResult> contentResults =
                contentQueryService.getContentsByIds(
                        contentFanRepository.findAllByUserId(userId).stream()
                                .map(ContentFan::getContentId)
                                .toList());
        List<ArtistResult> artistResults =
                artistQueryService.getArtistsByIds(
                        artistFanRepository.findAllByUserId(userId).stream()
                                .map(ArtistFan::getArtistId)
                                .toList());
        return MyFanResult.builder()
                .artists(artistResults.stream().map(FanTargetResult::from).toList())
                .contents(contentResults.stream().map(FanTargetResult::from).toList())
                .build();
    }

    @Override
    public Integer countArtistFansByUserId(Long userId) {
        return artistFanRepository.countArtistFansByUserId(userId);
    }

    @Override
    public Integer countContentFansByUserId(Long userId) {
        return contentFanRepository.countContentsFansByUserId(userId);
    }

    @Override
    public Integer countArtistFansByArtistId(Long artistId) {
        return artistFanRepository.countArtistFansByArtistId(artistId);
    }

    @Override
    public Integer countContentFanByContentId(Long contentId) {
        return contentFanRepository.countContentsFansByContentId(contentId);
    }

    @Override
    public ArtistFanViewResult getArtistFanView(Long userId, Long artistId) {
        return ArtistFanViewResult.of(
                countArtistFansByArtistId(artistId),
                userId != null
                        && artistFanRepository.existsById(new ArtistFanId(userId, artistId)));
    }

    @Override
    public ContentFanViewResult getContentFanView(Long userId, Long contentId) {
        return ContentFanViewResult.of(
                countContentFanByContentId(contentId),
                userId != null
                        && contentFanRepository.existsById(new ContentFanId(userId, contentId)));
    }
}
