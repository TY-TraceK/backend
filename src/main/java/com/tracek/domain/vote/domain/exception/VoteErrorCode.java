package com.tracek.domain.vote.domain.exception;

import com.tracek.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum VoteErrorCode implements BaseErrorCode {
    VOTE_NOT_FOUND(HttpStatus.NOT_FOUND, "VOTE_001", "존재하지 않는 투표 이력입니다."),
    VOTE_TARGET_NOT_FOUND(
            HttpStatus.NOT_FOUND, "VOTE_002", "존재하지 않거나 유효하지 않은 투표 대상(관광지/콘텐츠/아티스트 조합)입니다."),

    ALREADY_VOTED(HttpStatus.BAD_REQUEST, "VOTE_003", "해당 관광지에 이미 진행한 유효한 투표가 존재합니다."),
    VOTE_ALREADY_CANCELLED(HttpStatus.BAD_REQUEST, "VOTE_004", "이미 취소 처리된 투표입니다."),
    VOTE_CANNOT_BE_CANCELLED(HttpStatus.BAD_REQUEST, "VOTE_005", "취소할 수 없거나 취소 가능 기간이 지난 투표입니다."),
    UNAUTHORIZED_VOTE_ACCESS(HttpStatus.FORBIDDEN, "VOTE_006", "본인의 투표 이력만 취소하거나 수정할 수 있습니다."),

    CONCURRENT_VOTE_REQUEST(
            HttpStatus.TOO_MANY_REQUESTS, "VOTE_007", "현재 투표 처리 중입니다. 잠시 후 다시 시도해 주세요."),

    INVALID_VOTE_REQUEST(HttpStatus.BAD_REQUEST, "VOTE_008", "올바르지 않은 투표 요청 데이터입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
