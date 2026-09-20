package com.tracek.domain.artist.application.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tracek.domain.artist.domain.repository.ArtistRepository;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCanceledEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCreatedEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationUpdatedEvent;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ArtistVisitVerificationEventListenerTest {

    @Mock private ArtistRepository artistRepository;

    private ArtistVisitVerificationEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new ArtistVisitVerificationEventListener(artistRepository);
    }

    @Test
    @DisplayName("방문 인증 생성 이벤트를 받으면 해당 아티스트의 totalVerificationCount를 증가시킨다")
    void handle_createdEvent() {
        VisitVerificationCreatedEvent event =
                VisitVerificationCreatedEvent.builder().artistIds(Set.of(1L)).build();

        listener.handle(event);

        verify(artistRepository).increseVerificationCount(1L);
    }

    @Test
    @DisplayName("방문 인증 취소 이벤트를 받으면 해당 아티스트의 totalVerificationCount를 감소시킨다")
    void handle_canceledEvent() {
        VisitVerificationCanceledEvent event =
                VisitVerificationCanceledEvent.builder().artistIds(Set.of(1L)).build();

        listener.handle(event);

        verify(artistRepository).decreseVerificationCount(1L);
    }

    @Test
    @DisplayName("아티스트 없이 방문 인증한 경우(artistIds=빈 셋)에는 증가시키지 않는다")
    void handle_createdEvent_noArtist() {
        VisitVerificationCreatedEvent event =
                VisitVerificationCreatedEvent.builder().artistIds(Set.of()).build();

        listener.handle(event);

        verify(artistRepository, never()).increseVerificationCount(any());
    }

    @Test
    @DisplayName("아티스트 없이 방문 인증을 취소한 경우(artistIds=빈 셋)에는 감소시키지 않는다")
    void handle_canceledEvent_noArtist() {
        VisitVerificationCanceledEvent event =
                VisitVerificationCanceledEvent.builder().artistIds(Set.of()).build();

        listener.handle(event);

        verify(artistRepository, never()).decreseVerificationCount(any());
    }

    @Test
    @DisplayName("방문 인증 수정 이벤트에서 빠진 아티스트는 감소, 새로 추가된 아티스트는 증가시킨다")
    void handle_updatedEvent_artistsChanged() {
        VisitVerificationUpdatedEvent event =
                new VisitVerificationUpdatedEvent(
                        10L, 1L, Set.of(100L, 200L), 1L, Set.of(200L, 300L));

        listener.handle(event);

        verify(artistRepository).decreseVerificationCount(100L);
        verify(artistRepository).increseVerificationCount(300L);
        verify(artistRepository, never()).decreseVerificationCount(200L);
        verify(artistRepository, never()).increseVerificationCount(200L);
    }

    @Test
    @DisplayName("방문 인증 수정 이벤트에서 아티스트 구성이 그대로면 증감시키지 않는다")
    void handle_updatedEvent_artistsUnchanged() {
        VisitVerificationUpdatedEvent event =
                new VisitVerificationUpdatedEvent(10L, 1L, Set.of(100L), 2L, Set.of(100L));

        listener.handle(event);

        verify(artistRepository, never()).decreseVerificationCount(any());
        verify(artistRepository, never()).increseVerificationCount(any());
    }
}
