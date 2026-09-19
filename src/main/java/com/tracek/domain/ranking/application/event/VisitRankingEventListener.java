package com.tracek.domain.ranking.application.event;

import com.tracek.domain.ranking.application.service.VisitRankingProjectionService;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCanceledEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCreatedEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationUpdatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class VisitRankingEventListener {

    private final VisitRankingProjectionService commandService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VisitVerificationCreatedEvent event) {
        commandService.increase(event.locationId(), event.contentId(), event.artistIds());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VisitVerificationCanceledEvent event) {
        commandService.decrease(event.locationId(), event.contentId(), event.artistIds());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VisitVerificationUpdatedEvent event) {
        commandService.update(
                event.locationId(),
                event.previousContentId(),
                event.previousArtistIds(),
                event.updatedContentId(),
                event.updatedArtistIds());
    }
}
