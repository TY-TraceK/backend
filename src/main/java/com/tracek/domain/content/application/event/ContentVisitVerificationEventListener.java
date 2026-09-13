package com.tracek.domain.content.application.event;

import com.tracek.domain.content.domain.repository.ContentRepository;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCanceledEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ContentVisitVerificationEventListener {
    private final ContentRepository contentRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VisitVerificationCreatedEvent event) {
        contentRepository.increseVerificationCount(event.contentId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VisitVerificationCanceledEvent event) {
        contentRepository.decreseVerificationCount(event.contentId());
    }
}
