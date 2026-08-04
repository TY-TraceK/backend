package com.tracek.domain.content.domain.exception;

import com.tracek.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ContentErrorCode implements BaseErrorCode {
    CONTENT_NOT_FOUND(HttpStatus.NOT_FOUND, "CONTENT001", "해당 콘텐츠를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
