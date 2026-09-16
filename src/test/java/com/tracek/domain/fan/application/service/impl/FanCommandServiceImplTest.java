package com.tracek.domain.fan.application.service.impl;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tracek.domain.artist.application.service.ArtistQueryService;
import com.tracek.domain.artist.domain.exception.ArtistErrorCode;
import com.tracek.domain.artist.domain.model.Artist;
import com.tracek.domain.content.application.service.ContentQueryService;
import com.tracek.domain.content.domain.exception.ContentErrorCode;
import com.tracek.domain.content.domain.model.Content;
import com.tracek.domain.fan.domain.model.ArtistFan;
import com.tracek.domain.fan.domain.model.ArtistFanId;
import com.tracek.domain.fan.domain.model.ContentFan;
import com.tracek.domain.fan.domain.model.ContentFanId;
import com.tracek.domain.fan.domain.repository.ArtistFanRepository;
import com.tracek.domain.fan.domain.repository.ContentFanRepository;
import com.tracek.global.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class FanCommandServiceImplTest {

    @Mock private ArtistFanRepository artistFanRepository;

    @Mock private ContentFanRepository contentFanRepository;

    @Mock private ArtistQueryService artistQueryService;

    @Mock private ContentQueryService contentQueryService;

    @InjectMocks private FanCommandServiceImpl fanCommandService;

    @Test
    @DisplayName("아티스트가 존재하고 팬이 아니면 팬을 생성한다.")
    void createArtistFan_success() {
        // given
        Long userId = 1L;
        Long artistId = 10L;

        ArtistFanId fanId = new ArtistFanId(userId, artistId);

        when(artistQueryService.getArtistEntity(artistId)).thenReturn(mock(Artist.class));

        // existsById()의 boolean 기본값은 false

        // when
        fanCommandService.createArtistFan(userId, artistId);

        // then
        verify(artistQueryService).getArtistEntity(artistId);

        verify(artistFanRepository).existsById(eq(fanId));

        verify(artistFanRepository).saveAndFlush(any(ArtistFan.class));
    }

    @Test
    @DisplayName("이미 아티스트의 팬이면 다시 생성하지 않는다.")
    void createArtistFan_alreadyExists() {
        // given
        Long userId = 1L;
        Long artistId = 10L;

        ArtistFanId fanId = new ArtistFanId(userId, artistId);

        when(artistQueryService.getArtistEntity(artistId)).thenReturn(mock(Artist.class));

        when(artistFanRepository.existsById(fanId)).thenReturn(true);

        // when
        fanCommandService.createArtistFan(userId, artistId);

        // then
        verify(artistQueryService).getArtistEntity(artistId);

        verify(artistFanRepository).existsById(eq(fanId));

        verify(artistFanRepository, never()).saveAndFlush(any(ArtistFan.class));
    }

    @Test
    @DisplayName("동시 팬 등록으로 중복 제약 조건이 발생해도 정상 처리한다.")
    void createArtistFan_duplicateByConcurrency() {
        // given
        Long userId = 1L;
        Long artistId = 10L;

        ArtistFanId fanId = new ArtistFanId(userId, artistId);

        when(artistQueryService.getArtistEntity(artistId)).thenReturn(mock(Artist.class));

        /*
         * existsById() 조회 시점에는 팬이 없다고 가정한다.
         * Mockito boolean 기본값이 false이므로
         * 별도의 stubbing은 하지 않는다.
         */

        doThrow(new DataIntegrityViolationException("duplicate artist fan"))
                .when(artistFanRepository)
                .saveAndFlush(any(ArtistFan.class));

        // when & then
        assertDoesNotThrow(() -> fanCommandService.createArtistFan(userId, artistId));

        verify(artistQueryService).getArtistEntity(artistId);

        verify(artistFanRepository).existsById(eq(fanId));

        verify(artistFanRepository).saveAndFlush(any(ArtistFan.class));
    }

    @Test
    @DisplayName("존재하지 않는 아티스트의 팬은 생성할 수 없다.")
    void createArtistFan_artistNotFound() {
        // given
        Long userId = 1L;
        Long artistId = 999L;

        when(artistQueryService.getArtistEntity(artistId))
                .thenThrow(new CustomException(ArtistErrorCode.ARTIST_NOT_FOUND));

        // when & then
        assertThrows(
                CustomException.class, () -> fanCommandService.createArtistFan(userId, artistId));

        verify(artistQueryService).getArtistEntity(artistId);

        verify(artistFanRepository, never()).existsById(any(ArtistFanId.class));

        verify(artistFanRepository, never()).saveAndFlush(any(ArtistFan.class));
    }

    @Test
    @DisplayName("콘텐츠가 존재하고 팬이 아니면 팬을 생성한다.")
    void createContentFan_success() {
        // given
        Long userId = 1L;
        Long contentId = 20L;

        ContentFanId fanId = new ContentFanId(userId, contentId);

        when(contentQueryService.getContentEntity(contentId)).thenReturn(mock(Content.class));

        // existsById()의 boolean 기본값은 false

        // when
        fanCommandService.createContentFan(userId, contentId);

        // then
        verify(contentQueryService).getContentEntity(contentId);

        verify(contentFanRepository).existsById(eq(fanId));

        verify(contentFanRepository).saveAndFlush(any(ContentFan.class));
    }

    @Test
    @DisplayName("이미 콘텐츠의 팬이면 다시 생성하지 않는다.")
    void createContentFan_alreadyExists() {
        // given
        Long userId = 1L;
        Long contentId = 20L;

        ContentFanId fanId = new ContentFanId(userId, contentId);

        when(contentQueryService.getContentEntity(contentId)).thenReturn(mock(Content.class));

        when(contentFanRepository.existsById(fanId)).thenReturn(true);

        // when
        fanCommandService.createContentFan(userId, contentId);

        // then
        verify(contentQueryService).getContentEntity(contentId);

        verify(contentFanRepository).existsById(eq(fanId));

        verify(contentFanRepository, never()).saveAndFlush(any(ContentFan.class));
    }

    @Test
    @DisplayName("동시 팬 등록으로 중복 제약 조건이 발생해도 정상 처리한다.")
    void createContentFan_duplicateByConcurrency() {
        // given
        Long userId = 1L;
        Long contentId = 20L;

        ContentFanId fanId = new ContentFanId(userId, contentId);

        when(contentQueryService.getContentEntity(contentId)).thenReturn(mock(Content.class));

        /*
         * existsById() 조회 시점에는 팬이 없다고 가정한다.
         * Mockito boolean 기본값이 false이므로
         * 별도의 stubbing은 하지 않는다.
         */

        doThrow(new DataIntegrityViolationException("duplicate content fan"))
                .when(contentFanRepository)
                .saveAndFlush(any(ContentFan.class));

        // when & then
        assertDoesNotThrow(() -> fanCommandService.createContentFan(userId, contentId));

        verify(contentQueryService).getContentEntity(contentId);

        verify(contentFanRepository).existsById(eq(fanId));

        verify(contentFanRepository).saveAndFlush(any(ContentFan.class));
    }

    @Test
    @DisplayName("존재하지 않는 콘텐츠의 팬은 생성할 수 없다.")
    void createContentFan_contentNotFound() {
        // given
        Long userId = 1L;
        Long contentId = 999L;

        when(contentQueryService.getContentEntity(contentId))
                .thenThrow(new CustomException(ContentErrorCode.CONTENT_NOT_FOUND));

        // when & then
        assertThrows(
                CustomException.class, () -> fanCommandService.createContentFan(userId, contentId));

        verify(contentQueryService).getContentEntity(contentId);

        verify(contentFanRepository, never()).existsById(any(ContentFanId.class));

        verify(contentFanRepository, never()).saveAndFlush(any(ContentFan.class));
    }
}
