package com.tracek.domain.ranking.application.event;

import static org.mockito.Mockito.verify;

import com.tracek.domain.ranking.application.service.VisitRankingProjectionService;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCanceledEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCreatedEvent;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VisitVerificationEventListenerTest {

    @Mock private VisitRankingProjectionService commandService;

    private VisitVerificationEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new VisitVerificationEventListener(commandService);
    }

    @Test
    void handleCreatedEvent() {
        // given
        VisitVerificationCreatedEvent event =
                new VisitVerificationCreatedEvent(100L, 200L, LocalDateTime.now(), 1L, 3L, 2L);

        // when
        listener.handle(event);

        // then
        verify(commandService).increase(1L, 2L, 3L);
    }

    @Test
    void handleCanceledEvent() {
        // given
        VisitVerificationCanceledEvent event =
                new VisitVerificationCanceledEvent(100L, 200L, LocalDateTime.now(), 1L, 3L, 2L);

        // when
        listener.handle(event);

        // then
        verify(commandService).decrease(1L, 2L, 3L);
    }
}
