package com.tracek.domain.fan.application.service;

public interface FanCommandService {

    void createArtistFan(Long userId, Long artistId);

    void createContentFan(Long userId, Long artistId);

    void deleteArtistFan(Long userId, Long fanId);

    void deleteContentFan(Long userId, Long fanId);
}
