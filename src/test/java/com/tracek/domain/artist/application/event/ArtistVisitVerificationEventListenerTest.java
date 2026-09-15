package com.tracek.domain.artist.application.event;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tracek.domain.artist.domain.repository.ArtistRepository;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCanceledEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCreatedEvent;
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
                VisitVerificationCreatedEvent.builder().artistId(1L).build();

        listener.handle(event);

        verify(artistRepository).increseVerificationCount(1L);
    }

    @Test
    @DisplayName("방문 인증 취소 이벤트를 받으면 해당 아티스트의 totalVerificationCount를 감소시킨다")
    void handle_canceledEvent() {
        VisitVerificationCanceledEvent event =
                VisitVerificationCanceledEvent.builder().artistId(1L).build();

        listener.handle(event);

        verify(artistRepository).decreseVerificationCount(1L);
    }

    @Test
    @DisplayName("아티스트 없이 방문 인증한 경우(artistId=null)에는 증가시키지 않는다")
    void handle_createdEvent_noArtist() {
        VisitVerificationCreatedEvent event =
                VisitVerificationCreatedEvent.builder().artistId(null).build();

        listener.handle(event);

        verify(artistRepository, never()).increseVerificationCount(any());
    }

    @Test
    @DisplayName("아티스트 없이 방문 인증을 취소한 경우(artistId=null)에는 감소시키지 않는다")
    void handle_canceledEvent_noArtist() {
        VisitVerificationCanceledEvent event =
                VisitVerificationCanceledEvent.builder().artistId(null).build();

        listener.handle(event);

        verify(artistRepository, never()).decreseVerificationCount(any());
    }
}
