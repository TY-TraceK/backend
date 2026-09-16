package com.tracek.domain.fan.domain.exception;

import com.tracek.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum FanErrorCode implements BaseErrorCode {
    FAN_NOT_FOUND(HttpStatus.NOT_FOUND, "FAN001", "해당 팬 내역을 찾을 수 없습니다."),
    ACCESS_DINED(HttpStatus.FORBIDDEN, "FAN002", "본인의 이력만 취소하거나 수정할 수 있습니다.");
    private final HttpStatus status;
    private final String code;
    private final String message;
}
