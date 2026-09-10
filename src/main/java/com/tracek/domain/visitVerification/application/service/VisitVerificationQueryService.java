package com.tracek.domain.visitVerification.application.service;

import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationHistoriesSearchCondition;
import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationStatusSearchCondition;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationStatusSearchResult;

public interface VisitVerificationQueryService {

    VisitVerificationStatusSearchResult getMyVisitVerificationStatus(
            VisitVerificationStatusSearchCondition condition);

    VisitVerificationHistoriesResult getMyHistories(
            VisitVerificationHistoriesSearchCondition condition);
}
