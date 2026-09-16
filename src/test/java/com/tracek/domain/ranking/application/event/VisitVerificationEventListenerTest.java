package com.tracek.domain.ranking.application.event;

import static org.mockito.Mockito.verify;

import com.tracek.domain.ranking.application.service.VisitRankingProjectionService;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCanceledEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCreatedEvent;
import java.time.LocalDateTime;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VisitVerificationEventListenerTest {

    @Mock private VisitRankingProjectionService commandService;

    private VisitRankingEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new VisitRankingEventListener(commandService);
    }

    @Test
    void handleCreatedEvent() {
        // given
        VisitVerificationCreatedEvent event =
                VisitVerificationCreatedEvent.builder()
                        .visitVerificationId(100L)
                        .visitVerificationOwner(200L)
                        .visitVerifiedAt(LocalDateTime.now())
                        .locationId(1L)
                        .contentId(2L)
                        .artistIds(Set.of(3L))
                        .build();
        listener.handle(event);

        // then
        verify(commandService).increase(1L, 2L, Set.of(3L));
    }

    @Test
    void handleCanceledEvent() {
        // given
        VisitVerificationCanceledEvent event =
                new VisitVerificationCanceledEvent(100L, 200L, LocalDateTime.now(), 1L, 3L, 2L);

        // when
        listener.handle(event);

        // then
        verify(commandService).decrease(1L, 2L, Set.of(3L));
    }
}
