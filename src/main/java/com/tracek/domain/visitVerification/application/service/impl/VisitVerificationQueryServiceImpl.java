package com.tracek.domain.visitVerification.application.service.impl;

import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationHistoriesSearchCondition;
import com.tracek.domain.visitVerification.application.dto.condition.VisitVerificationStatusSearchCondition;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationHistoriesResult;
import com.tracek.domain.visitVerification.application.dto.result.VisitVerificationStatusSearchResult;
import com.tracek.domain.visitVerification.application.service.VisitVerificationQueryService;
import com.tracek.domain.visitVerification.domain.model.VisitVerificationHistoryCriteria;
import com.tracek.domain.visitVerification.domain.repository.VisitVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VisitVerificationQueryServiceImpl implements VisitVerificationQueryService {

    private final VisitVerificationRepository visitVerificationRepository;

    @Override
    public VisitVerificationStatusSearchResult getMyVisitVerificationStatus(
            VisitVerificationStatusSearchCondition condition) {
        return VisitVerificationStatusSearchResult.from(
                visitVerificationRepository
                        .findUserLocationVerifiedByDate(
                                condition.userId(), condition.locationId(), condition.date())
                        .orElse(null),
                condition.date());
    }

    @Override
    public VisitVerificationHistoriesResult getMyHistories(
            VisitVerificationHistoriesSearchCondition condition) {
        return VisitVerificationHistoriesResult.of(
                visitVerificationRepository.findHistoriesByCriteria(
                        VisitVerificationHistoryCriteria.builder()
                                .userId(condition.userId())
                                .contentId(condition.contentId())
                                .artistId(condition.artistId())
                                .locationId(condition.locationId())
                                .startDate(condition.startDate())
                                .endDate(condition.endDate())
                                .status(condition.status())
                                .cursorDate(condition.cursorDate())
                                .city(condition.city())
                                .size(condition.size())
                                .build()),
                condition.size());
    }
}
