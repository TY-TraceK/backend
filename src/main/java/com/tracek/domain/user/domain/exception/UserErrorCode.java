package com.tracek.domain.user.domain.exception;

import com.tracek.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements BaseErrorCode {
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "해당하는 유저가 없습니다."),
    USER_NOT_AUTHORIZED(HttpStatus.UNAUTHORIZED, "USER_002", "해당 유저는 접근 권한이 없습니다."),
    USER_NOT_ACTIVATED(HttpStatus.SERVICE_UNAVAILABLE, "USER_003", "해당 유저는 활성화 상태가 아닙니다."),
    INVALID_EMAIL_FORMAT(HttpStatus.BAD_REQUEST, "USER_004", "올바르지 않은 이메일 형식입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
