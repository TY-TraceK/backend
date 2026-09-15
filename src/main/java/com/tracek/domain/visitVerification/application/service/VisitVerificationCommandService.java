package com.tracek.domain.visitVerification.application.service;

import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCancelCommand;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCreateCommand;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationUpdateCommand;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCancelResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCreateResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationUpdateResult;
import jakarta.transaction.Transactional;

public interface VisitVerificationCommandService {

    VisitVerificationCreateResult createVisitVerification(VisitVerificationCreateCommand command);

    VisitVerificationCancelResult cancelVisitVerification(VisitVerificationCancelCommand command);

    @Transactional
    VisitVerificationUpdateResult updateVisitVerification(VisitVerificationUpdateCommand command);
}
