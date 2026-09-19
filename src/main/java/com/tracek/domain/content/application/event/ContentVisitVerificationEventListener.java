package com.tracek.domain.content.application.event;

import com.tracek.domain.content.domain.repository.ContentRepository;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCanceledEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCreatedEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationUpdatedEvent;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ContentVisitVerificationEventListener {
    private final ContentRepository contentRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VisitVerificationCreatedEvent event) {
        if (event.contentId() == null) {
            return;
        }
        contentRepository.increseVerificationCount(event.contentId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VisitVerificationCanceledEvent event) {
        if (event.contentId() == null) {
            return;
        }
        contentRepository.decreseVerificationCount(event.contentId());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VisitVerificationUpdatedEvent event) {
        if (Objects.equals(event.previousContentId(), event.updatedContentId())) {
            return;
        }
        if (event.previousContentId() != null) {
            contentRepository.decreseVerificationCount(event.previousContentId());
        }
        if (event.updatedContentId() != null) {
            contentRepository.increseVerificationCount(event.updatedContentId());
        }
    }
}
