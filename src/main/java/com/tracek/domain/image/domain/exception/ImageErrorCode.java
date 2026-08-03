package com.tracek.domain.image.domain.exception;

import com.tracek.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ImageErrorCode implements BaseErrorCode {
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE001", "해당 이미지를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
