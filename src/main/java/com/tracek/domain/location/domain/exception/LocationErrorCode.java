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
    INVALID_ADDRESS(HttpStatus.BAD_REQUEST, "LOC003", "상세 주소 정보는 필수 입력값입니다."),
    INVALID_IMAGE_URL(HttpStatus.BAD_REQUEST, "LOC004", "올바른 URL 형식(http/https)이 아닙니다."),
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "LOC005", "올바르지 않은 관광지 카테고리입니다."),
    MAPPING_NOT_FOUND(HttpStatus.NOT_FOUND, "LOC006", "해당 매핑 정보를 찾을 수 없습니다."),
    INVALID_BOUNDS(HttpStatus.BAD_REQUEST, "LOC007", "올바르지 않은 지도 범위(bounds)입니다."),
    BOUNDS_TOO_WIDE(HttpStatus.BAD_REQUEST, "LOC008", "지도 범위가 너무 넓습니다. 화면을 확대한 후 다시 조회해주세요.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
