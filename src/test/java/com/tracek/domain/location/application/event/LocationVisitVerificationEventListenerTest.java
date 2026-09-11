package com.tracek.domain.location.application.event;

import static org.mockito.Mockito.verify;

import com.tracek.domain.location.domain.repository.LocationRepository;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCanceledEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCreatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LocationVisitVerificationEventListenerTest {

    @Mock private LocationRepository locationRepository;

    private LocationVisitVerificationEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new LocationVisitVerificationEventListener(locationRepository);
    }

    @Test
    @DisplayName("방문 인증 생성 이벤트를 받으면 해당 장소의 totalVerificationCount를 증가시킨다")
    void handle_createdEvent() {
        VisitVerificationCreatedEvent event =
                VisitVerificationCreatedEvent.builder().locationId(1L).build();

        listener.handle(event);

        verify(locationRepository).increseVerificationCount(1L);
    }

    @Test
    @DisplayName("방문 인증 취소 이벤트를 받으면 해당 장소의 totalVerificationCount를 감소시킨다")
    void handle_canceledEvent() {
        VisitVerificationCanceledEvent event =
                VisitVerificationCanceledEvent.builder().locationId(1L).build();

        listener.handle(event);

        verify(locationRepository).decreseVerificationCount(1L);
    }
}
