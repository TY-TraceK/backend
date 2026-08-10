package com.tracek.domain.location.domain.exception;

import com.tracek.global.response.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum LocationErrorCode implements BaseErrorCode {
    LOCATION_NOT_FOUND(HttpStatus.NOT_FOUND, "LOC001", "해당 관광지를 찾을 수 없습니다."),
    INVALID_GEO_LOCATION(HttpStatus.BAD_REQUEST, "LOC002", "올바르지 않은 위경도 좌표 범위입니다."),
    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "LOCATION003", "상세 주소 정보는 필수 입력값입니다."),
    INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST, "LOCATION004", "올바른 URL 형식(http/https)이 아닙니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "LOCATION005", "올바르지 않은 관광지 카테고리입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
