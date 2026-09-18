package com.tracek.domain.content.application.event;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.tracek.domain.content.domain.repository.ContentRepository;
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

    @Test
    @DisplayName("방문 인증 수정 이벤트로 콘텐츠가 바뀌면 이전 콘텐츠는 감소, 새 콘텐츠는 증가시킨다")
    void handle_updatedEvent_contentChanged() {
        VisitVerificationUpdatedEvent event =
                new VisitVerificationUpdatedEvent(10L, 1L, Set.of(100L), 2L, Set.of(100L));

        listener.handle(event);

        verify(contentRepository).decreseVerificationCount(1L);
        verify(contentRepository).increseVerificationCount(2L);
    }

    @Test
    @DisplayName("방문 인증 수정 이벤트로 콘텐츠가 그대로면 증감시키지 않는다")
    void handle_updatedEvent_contentUnchanged() {
        VisitVerificationUpdatedEvent event =
                new VisitVerificationUpdatedEvent(10L, 1L, Set.of(100L), 1L, Set.of(200L));

        listener.handle(event);

        verify(contentRepository, never()).decreseVerificationCount(1L);
        verify(contentRepository, never()).increseVerificationCount(1L);
    }
}
