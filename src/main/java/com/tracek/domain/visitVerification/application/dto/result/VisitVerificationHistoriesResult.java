package com.tracek.domain.visitVerification.application.dto.result;

import com.tracek.domain.visitVerification.domain.model.VisitVerification;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record VisitVerificationHistoriesResult(
        Map<LocalDate, List<VisitVerificationHistoriesIndividualResult>> histories,
        boolean hasNext,
        LocalDate nextCursorDate) {

    public static VisitVerificationHistoriesResult of(
            List<VisitVerification> visitVerifications, int requestedSize) {

        if (visitVerifications.isEmpty()) {
            return VisitVerificationHistoriesResult.builder()
                    .histories(Map.of())
                    .hasNext(false)
                    .nextCursorDate(null)
                    .build();
        }

        // 조회된 데이터에서 고유한 날짜 목록 순서대로 추출
        List<LocalDate> distinctDates =
                visitVerifications.stream()
                        .map(VisitVerification::getValidVerifiedAt)
                        .distinct()
                        .toList();

        boolean hasNext = false;
        LocalDate nextCursorDate = null;
        List<VisitVerification> targetVerifications = visitVerifications;

        // 고유 날짜 개수가 요청한 size보다 많다면, 딱 size만큼의 날짜까지만 허용하고 자름
        if (distinctDates.size() > requestedSize) {
            hasNext = true;
            List<LocalDate> allowedDates = distinctDates.subList(0, requestedSize);

            targetVerifications =
                    visitVerifications.stream()
                            .filter(v -> allowedDates.contains(v.getValidVerifiedAt()))
                            .toList();

            nextCursorDate = allowedDates.getLast();
        } else {
            nextCursorDate = distinctDates.getLast();
        }

        // 허용된 데이터들을 날짜별로 그룹화
        // 정렬 순서 유지를 위해 LinkedHashMap 사용
        Map<LocalDate, List<VisitVerificationHistoriesIndividualResult>> groupedHistories =
                targetVerifications.stream()
                        .map(VisitVerificationHistoriesIndividualResult::from)
                        .collect(
                                Collectors.groupingBy(
                                        VisitVerificationHistoriesIndividualResult
                                                ::visitVerifiedDate,
                                        LinkedHashMap::new,
                                        Collectors.toList()));

        return VisitVerificationHistoriesResult.builder()
                .histories(groupedHistories)
                .hasNext(hasNext)
                .nextCursorDate(nextCursorDate)
                .build();
    }
}
