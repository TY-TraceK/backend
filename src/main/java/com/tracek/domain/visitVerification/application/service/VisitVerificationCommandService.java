package com.tracek.domain.visitVerification.application.service;

import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCancelCommand;
import com.tracek.domain.visitVerification.application.dto.command.VisitVerificationCreateCommand;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCancelResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationCreateResult;

public interface VisitVerificationCommandService {

    VisitVerificationCreateResult createVisitVerification(VisitVerificationCreateCommand command);

    VisitVerificationCancelResult cancelVisitVerification(VisitVerificationCancelCommand command);
}
