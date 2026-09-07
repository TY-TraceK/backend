package com.tracek.domain.ranking.application.event;

import com.tracek.domain.ranking.application.service.VisitRankingProjectionService;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCanceledEvent;
import com.tracek.domain.visitVerification.application.event.VisitVerificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VisitVerificationEventListener {

  private final VisitRankingProjectionService commandService;

  @EventListener()
  public void handle(VisitVerificationCreatedEvent event) {
    commandService.increase(event.locationId(), event.contentId(), event.artistId());
  }

  @EventListener()
  public void handle(VisitVerificationCanceledEvent event) {
    commandService.decrease(event.locationId(), event.contentId(), event.artistId());
  }
}
