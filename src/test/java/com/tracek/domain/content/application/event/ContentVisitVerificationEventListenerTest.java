package com.tracek.domain.content.application.event;

import static org.mockito.Mockito.verify;

import com.tracek.domain.content.domain.repository.ContentRepository;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCanceledEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContentVisitVerificationEventListenerTest {

    @Mock private ContentRepository contentRepository;

    private ContentVisitVerificationEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new ContentVisitVerificationEventListener(contentRepository);
    }

    @Test
    @DisplayName("방문 인증 생성 이벤트를 받으면 해당 콘텐츠의 totalVerificationCount를 증가시킨다")
    void handle_createdEvent() {
        VisitVerificationCreatedEvent event =
                VisitVerificationCreatedEvent.builder().contentId(1L).build();

        listener.handle(event);

        verify(contentRepository).increseVerificationCount(1L);
    }

    @Test
    @DisplayName("방문 인증 취소 이벤트를 받으면 해당 콘텐츠의 totalVerificationCount를 감소시킨다")
    void handle_canceledEvent() {
        VisitVerificationCanceledEvent event =
                VisitVerificationCanceledEvent.builder().contentId(1L).build();

        listener.handle(event);

        verify(contentRepository).decreseVerificationCount(1L);
    }
}
