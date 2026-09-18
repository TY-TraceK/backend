package com.tracek.domain.fan.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FanQueryServiceImplTest {

    @Mock private ContentFanRepository contentFanRepository;

    @Mock private ContentQueryService contentQueryService;

    @Mock private ArtistFanRepository artistFanRepository;

    @Mock private ArtistQueryService artistQueryService;

    @InjectMocks private FanQueryServiceImpl fanQueryService;

    @Test
    @DisplayName("사용자가 팬으로 등록한 콘텐츠와 아티스트 목록을 조회한다.")
    void getMyFanTargets_success() {
        // given
        Long userId = 1L;

        ContentFan contentFan1 = ContentFan.create(new ContentFanId(userId, 10L));
        ContentFan contentFan2 = ContentFan.create(new ContentFanId(userId, 20L));

        ArtistFan artistFan1 = ArtistFan.create(new ArtistFanId(userId, 30L));
        ArtistFan artistFan2 = ArtistFan.create(new ArtistFanId(userId, 40L));

        ContentResult contentResult1 = org.mockito.Mockito.mock(ContentResult.class);
        ContentResult contentResult2 = org.mockito.Mockito.mock(ContentResult.class);

        ArtistResult artistResult1 = org.mockito.Mockito.mock(ArtistResult.class);
        ArtistResult artistResult2 = org.mockito.Mockito.mock(ArtistResult.class);

        when(contentFanRepository.findAllByUserId(userId))
                .thenReturn(List.of(contentFan1, contentFan2));

        when(artistFanRepository.findAllByUserId(userId))
                .thenReturn(List.of(artistFan1, artistFan2));

        when(contentQueryService.getContentsByIds(List.of(10L, 20L)))
                .thenReturn(List.of(contentResult1, contentResult2));

        when(artistQueryService.getArtistsByIds(List.of(30L, 40L)))
                .thenReturn(List.of(artistResult1, artistResult2));

        // FanTargetResult.from()에서 사용하는 값
        when(contentResult1.getContentId()).thenReturn(10L);
        when(contentResult1.getTitle()).thenReturn("콘텐츠1");
        when(contentResult1.getDescription()).thenReturn("설명1");
        when(contentResult1.getPictureUrl()).thenReturn("content1.jpg");
        when(contentResult1.getFanCount()).thenReturn(100L);
        when(contentResult1.getTotalVerificationCount()).thenReturn(50L);

        when(contentResult2.getContentId()).thenReturn(20L);
        when(contentResult2.getTitle()).thenReturn("콘텐츠2");
        when(contentResult2.getDescription()).thenReturn("설명2");
        when(contentResult2.getPictureUrl()).thenReturn("content2.jpg");
        when(contentResult2.getFanCount()).thenReturn(200L);
        when(contentResult2.getTotalVerificationCount()).thenReturn(70L);

        when(artistResult1.getId()).thenReturn(30L);
        when(artistResult1.getName()).thenReturn("아티스트1");
        when(artistResult1.getAlias()).thenReturn("별명1");
        when(artistResult1.getPictureUrl()).thenReturn("artist1.jpg");
        when(artistResult1.getFanCount()).thenReturn(300L);
        when(artistResult1.getTotalVerificationCount()).thenReturn(80L);

        when(artistResult2.getId()).thenReturn(40L);
        when(artistResult2.getName()).thenReturn("아티스트2");
        when(artistResult2.getAlias()).thenReturn("별명2");
        when(artistResult2.getPictureUrl()).thenReturn("artist2.jpg");
        when(artistResult2.getFanCount()).thenReturn(400L);
        when(artistResult2.getTotalVerificationCount()).thenReturn(90L);

        // when
        MyFanResult result = fanQueryService.getMyFanTargets(userId);

        // then
        List<FanTargetResult> contents = result.contents();
        List<FanTargetResult> artists = result.artists();

        assertThat(contents).hasSize(2);
        assertThat(artists).hasSize(2);

        assertThat(contents.get(0).id()).isEqualTo(10L);
        assertThat(contents.get(0).name()).isEqualTo("콘텐츠1");
        assertThat(contents.get(0).description()).isEqualTo("설명1");
        assertThat(contents.get(0).pictureUrl()).isEqualTo("content1.jpg");
        assertThat(contents.get(0).fanCount()).isEqualTo(100L);
        assertThat(contents.get(0).totalVerificationCount()).isEqualTo(50L);

        assertThat(artists.get(0).id()).isEqualTo(30L);
        assertThat(artists.get(0).name()).isEqualTo("아티스트1");
        assertThat(artists.get(0).description()).isEqualTo("별명1");
        assertThat(artists.get(0).pictureUrl()).isEqualTo("artist1.jpg");
        assertThat(artists.get(0).fanCount()).isEqualTo(300L);
        assertThat(artists.get(0).totalVerificationCount()).isEqualTo(80L);

        verify(contentFanRepository).findAllByUserId(userId);
        verify(artistFanRepository).findAllByUserId(userId);
        verify(contentQueryService).getContentsByIds(List.of(10L, 20L));
        verify(artistQueryService).getArtistsByIds(List.of(30L, 40L));
    }

    @Test
    @DisplayName("팬으로 등록한 대상이 없으면 빈 콘텐츠 목록과 빈 아티스트 목록을 반환한다.")
    void getMyFanTargets_empty() {
        // given
        Long userId = 1L;

        when(contentFanRepository.findAllByUserId(userId)).thenReturn(List.of());

        when(artistFanRepository.findAllByUserId(userId)).thenReturn(List.of());

        when(contentQueryService.getContentsByIds(List.of())).thenReturn(List.of());

        when(artistQueryService.getArtistsByIds(List.of())).thenReturn(List.of());

        // when
        MyFanResult result = fanQueryService.getMyFanTargets(userId);

        // then
        assertThat(result.contents()).isEmpty();
        assertThat(result.artists()).isEmpty();
    }

    @Test
    @DisplayName("사용자가 팬으로 등록한 아티스트 수를 조회한다.")
    void countArtistFansByUserId_success() {
        // given
        Long userId = 1L;

        when(artistFanRepository.countArtistFansByUserId(userId)).thenReturn(3);

        // when
        Integer result = fanQueryService.countArtistFansByUserId(userId);

        // then
        assertThat(result).isEqualTo(3);

        verify(artistFanRepository).countArtistFansByUserId(userId);
    }

    @Test
    @DisplayName("사용자가 팬으로 등록한 콘텐츠 수를 조회한다.")
    void countContentFansByUserId_success() {
        // given
        Long userId = 1L;

        when(contentFanRepository.countContentsFansByUserId(userId)).thenReturn(4);

        // when
        Integer result = fanQueryService.countContentFansByUserId(userId);

        // then
        assertThat(result).isEqualTo(4);

        verify(contentFanRepository).countContentsFansByUserId(userId);
    }

    @Test
    @DisplayName("아티스트의 전체 팬 수를 조회한다.")
    void countArtistFansByArtistId_success() {
        // given
        Long artistId = 10L;

        when(artistFanRepository.countArtistFansByArtistId(artistId)).thenReturn(100);

        // when
        Integer result = fanQueryService.countArtistFansByArtistId(artistId);

        // then
        assertThat(result).isEqualTo(100);

        verify(artistFanRepository).countArtistFansByArtistId(artistId);
    }

    @Test
    @DisplayName("콘텐츠의 전체 팬 수를 조회한다.")
    void countContentFanByContentId_success() {
        // given
        Long contentId = 20L;

        when(contentFanRepository.countContentsFansByContentId(contentId)).thenReturn(200);

        // when
        Integer result = fanQueryService.countContentFanByContentId(contentId);

        // then
        assertThat(result).isEqualTo(200);

        verify(contentFanRepository).countContentsFansByContentId(contentId);
    }

    @Test
    @DisplayName("로그인 사용자가 아티스트의 팬이면 팬 수와 isFan true를 반환한다.")
    void getArtistFanView_fan() {
        // given
        Long userId = 1L;
        Long artistId = 10L;

        ArtistFanId fanId = new ArtistFanId(userId, artistId);

        when(artistFanRepository.countArtistFansByArtistId(artistId)).thenReturn(100);

        when(artistFanRepository.existsById(fanId)).thenReturn(true);

        // when
        ArtistFanViewResult result = fanQueryService.getArtistFanView(userId, artistId);

        // then
        assertThat(result.fanCount()).isEqualTo(100);
        assertThat(result.isFan()).isTrue();

        verify(artistFanRepository).existsById(fanId);
    }

    @Test
    @DisplayName("로그인 사용자가 아티스트의 팬이 아니면 isFan false를 반환한다.")
    void getArtistFanView_notFan() {
        // given
        Long userId = 1L;
        Long artistId = 10L;

        ArtistFanId fanId = new ArtistFanId(userId, artistId);

        when(artistFanRepository.countArtistFansByArtistId(artistId)).thenReturn(100);

        when(artistFanRepository.existsById(fanId)).thenReturn(false);

        // when
        ArtistFanViewResult result = fanQueryService.getArtistFanView(userId, artistId);

        // then
        assertThat(result.fanCount()).isEqualTo(100);
        assertThat(result.isFan()).isFalse();

        verify(artistFanRepository).existsById(fanId);
    }

    @Test
    @DisplayName("비로그인 사용자가 아티스트 팬 정보를 조회하면 isFan false를 반환한다.")
    void getArtistFanView_anonymous() {
        // given
        Long artistId = 10L;

        when(artistFanRepository.countArtistFansByArtistId(artistId)).thenReturn(100);

        // when
        ArtistFanViewResult result = fanQueryService.getArtistFanView(null, artistId);

        // then
        assertThat(result.fanCount()).isEqualTo(100);
        assertThat(result.isFan()).isFalse();

        verify(artistFanRepository, never()).existsById(any(ArtistFanId.class));
    }

    @Test
    @DisplayName("로그인 사용자가 콘텐츠의 팬이면 팬 수와 isFan true를 반환한다.")
    void getContentFanView_fan() {
        // given
        Long userId = 1L;
        Long contentId = 20L;

        ContentFanId fanId = new ContentFanId(userId, contentId);

        when(contentFanRepository.countContentsFansByContentId(contentId)).thenReturn(200);

        when(contentFanRepository.existsById(fanId)).thenReturn(true);

        // when
        ContentFanViewResult result = fanQueryService.getContentFanView(userId, contentId);

        // then
        assertThat(result.fanCount()).isEqualTo(200);
        assertThat(result.isFan()).isTrue();

        verify(contentFanRepository).existsById(fanId);
    }

    @Test
    @DisplayName("로그인 사용자가 콘텐츠의 팬이 아니면 isFan false를 반환한다.")
    void getContentFanView_notFan() {
        // given
        Long userId = 1L;
        Long contentId = 20L;

        ContentFanId fanId = new ContentFanId(userId, contentId);

        when(contentFanRepository.countContentsFansByContentId(contentId)).thenReturn(200);

        when(contentFanRepository.existsById(fanId)).thenReturn(false);

        // when
        ContentFanViewResult result = fanQueryService.getContentFanView(userId, contentId);

        // then
        assertThat(result.fanCount()).isEqualTo(200);
        assertThat(result.isFan()).isFalse();

        verify(contentFanRepository).existsById(fanId);
    }

    @Test
    @DisplayName("비로그인 사용자가 콘텐츠 팬 정보를 조회하면 isFan false를 반환한다.")
    void getContentFanView_anonymous() {
        // given
        Long contentId = 20L;

        when(contentFanRepository.countContentsFansByContentId(contentId)).thenReturn(200);

        // when
        ContentFanViewResult result = fanQueryService.getContentFanView(null, contentId);

        // then
        assertThat(result.fanCount()).isEqualTo(200);
        assertThat(result.isFan()).isFalse();

        verify(contentFanRepository, never()).existsById(any(ContentFanId.class));
    }
}
