package com.tracek.domain.artist.application.event;

import com.tracek.domain.artist.domain.repository.ArtistRepository;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCanceledEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCreatedEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationUpdatedEvent;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class ArtistVisitVerificationEventListener {
    private final ArtistRepository artistRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VisitVerificationCreatedEvent event) {
        event.artistIds().forEach(artistRepository::increseVerificationCount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VisitVerificationCanceledEvent event) {
        event.artistIds().forEach(artistRepository::decreseVerificationCount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(VisitVerificationUpdatedEvent event) {
        Set<Long> removedArtistIds = new HashSet<>(event.previousArtistIds());
        removedArtistIds.removeAll(event.updatedArtistIds());

        Set<Long> addedArtistIds = new HashSet<>(event.updatedArtistIds());
        addedArtistIds.removeAll(event.previousArtistIds());

        removedArtistIds.forEach(artistRepository::decreseVerificationCount);
        addedArtistIds.forEach(artistRepository::increseVerificationCount);
    }
}
