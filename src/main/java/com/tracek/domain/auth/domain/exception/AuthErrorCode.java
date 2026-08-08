package com.tracek.domain.auth.domain.exception;

import com.tracek.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {
    PROVIDER_NOT_FOUND(HttpStatus.NOT_FOUND, "AUTH_001", "제공하지 않는 소셜입니다."),
    OAUTH_CODE_INVALID(HttpStatus.BAD_REQUEST, "AUTH_002", "Oauth의 접근 코드가 올바르지 않습니다."),
    OAUTH_USER_INVALID(HttpStatus.BAD_REQUEST, "AUTH_003", "Oauth 유저 정보가 올바르지 않습니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
