package com.tracek.global.response;

import com.tracek.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SecurityErrorCode implements BaseErrorCode {
    INVALID_SIGNATURE(HttpStatus.UNAUTHORIZED, "SECURITY_001", "잘못된 JWT 서명입니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "SECURITY_002", "만료된 JWT 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.UNAUTHORIZED, "SECURITY_003", "지원되지 않는 JWT 토큰입니다."),
    EMPTY_CLAIMS(HttpStatus.BAD_REQUEST, "SECURITY_004", "JWT 토큰이 비어있거나 잘못되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "SECURITY_005", "유효하지 않은 토큰입니다."),
    TOKEN_BLACKLISTED(HttpStatus.FORBIDDEN, "SECURITY_006", "로그아웃된 토큰입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
