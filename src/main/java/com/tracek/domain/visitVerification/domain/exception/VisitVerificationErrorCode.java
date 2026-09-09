package com.tracek.domain.visitVerification.domain.exception;

import com.tracek.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VisitVerificationErrorCode implements BaseErrorCode {
    VISIT_VERIFICATION_NOT_FOUND(
            HttpStatus.NOT_FOUND, "visitVerification_001", "존재하지 않는 방문 인증 이력입니다."),
    VERIFICATION_TARGET_NOT_FOUND(
            HttpStatus.NOT_FOUND,
            "visitVerification_002",
            "존재하지 않거나 유효하지 않은 방문인증 대상(관광지/콘텐츠/아티스트 조합)입니다."),

    ALREADY_VERIFIED(
            HttpStatus.BAD_REQUEST, "visitVerification_003", "해당 관광지에 이미 진행한 유효한 방문 인증이 존재합니다."),
    CANNOT_BE_CANCELLED(
            HttpStatus.BAD_REQUEST, "visitVerification_005", "취소할 수 없거나 취소 가능 기간이 지난 방문 인증입니다."),
    ACCESS_DINED(HttpStatus.FORBIDDEN, "visitVerification_006", "본인의 방문 인증 이력만 취소하거나 수정할 수 있습니다."),

    CONCURRENT_REQUEST(
            HttpStatus.TOO_MANY_REQUESTS,
            "visitVerification_007",
            "현재 방문 인증 처리 중입니다. 잠시 후 다시 시도해 주세요."),

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "visitVerification_008", "올바르지 않은 방문 인증 요청 데이터입니다."),
    INVALID_DATE(HttpStatus.BAD_REQUEST, "visitVerification_009", "시작일은 종료일보다 늦을 수 없습니다."),
    VISIT_ZONE_MISMATCH(HttpStatus.BAD_REQUEST, "visitVerification_010", "방문 인증 할 수 있는 위치가 아닙니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
