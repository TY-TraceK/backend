package com.tracek.domain.fan.application.service;

import com.tracek.domain.fan.application.dto.result.ArtistFanViewResult;
import com.tracek.domain.fan.application.dto.result.ContentFanViewResult;
import com.tracek.domain.fan.application.dto.result.MyFanResult;

public interface FanQueryService {

    MyFanResult getMyFanTargets(Long userId);

    Integer countArtistFansByUserId(Long userId);

    Integer countContentFansByUserId(Long userId);

    Integer countArtistFansByArtistId(Long artistId);

    Integer countContentFanByContentId(Long contentId);

    ArtistFanViewResult getArtistFanView(Long userId, Long artistId);

    ContentFanViewResult getContentFanView(Long userId, Long contentId);
}
